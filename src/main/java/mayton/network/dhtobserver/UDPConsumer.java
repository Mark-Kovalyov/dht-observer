package mayton.network.dhtobserver;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.Ping;
import mayton.network.dhtobserver.jfr.DhtParseEvent;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import the8472.bencode.BDecoder;
import the8472.bencode.BEncoder;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static mayton.network.dhtobserver.Utils.binhex;

public class UDPConsumer implements Runnable {

    // TODO: How many injectors???
    private Injector injector = Guice.createInjector(new DhtObserverModule());

    private Chronicler chronicler;

    private AtomicInteger packetsReceived = new AtomicInteger(0);

    private Logger logger = LogManager.getLogger(UDPConsumer.class);

    private BlockingQueue<Triple<byte[], InetAddress, Integer>> udpPackets;

    private String threadName;

    private String shortCode; // Thiss is need for logging

    private String destPath = "/bigdata/dht-observer/out";

    public UDPConsumer(BlockingQueue<Triple<byte[], InetAddress, Integer>> udpPackets, String threadName, String shortCode) {
        this.udpPackets = udpPackets;
        this.threadName = threadName;
        this.shortCode = shortCode;
        new File(destPath + "/decoded").mkdirs();
        new File(destPath + "/non-decoded").mkdirs();
        ThreadContext.put("shortCode", shortCode);
        logger = LogManager.getLogger("dhtlisteners." + shortCode);
    }


    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                logger.trace("Consume...");
                Triple<byte[], InetAddress, Integer> item = udpPackets.take();
                byte[] buf = item.getLeft();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, item.getMiddle(), item.getRight());
                DhtParseEvent dhtParseEvent = new DhtParseEvent();
                dhtParseEvent.shortCode = shortCode;
                dhtParseEvent.begin();
                decodeCommand(packet);
                dhtParseEvent.end();
                dhtParseEvent.commit();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("", e);
            }
        }
    }

    void decodeCommand(DatagramPacket packet) {
        Chronicler chronicler = injector.getInstance(Chronicler.class);
        packetsReceived.incrementAndGet();
        byte[] packetData = packet.getData();
        String uuid = UUID.randomUUID().toString();
        try{
            BDecoder decoder = new BDecoder();
            Map<String, Object> res = decoder.decode(ByteBuffer.wrap(packetData));
            logger.info("{} :: Received DHT UDP packet : from {}:{} ({})",
                    threadName,
                    packet.getAddress(),
                    "{}", //geoDb().decodeCountryCity(packet.getAddress().getHostAddress()),
                    packet.getPort()
            );

            Optional<Ping> pingCommandOptional = tryToExtractPingCommand(res);
            if (pingCommandOptional.isPresent()) {
                // ping Query = {"t":"aa", "y":"q", "q":"ping", "a":{"id":"abcdefghij0123456789"}}
                // bencoded = d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe
                //
                // Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
                // bencoded = d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re
                logger.info("Ping detected");
                DatagramSocket socket = new DatagramSocket();
                BEncoder encoder = new BEncoder();
                // Alphabet order of keys!!
                Map<String, Object> sendMap = new TreeMap<>();
                    sendMap.put("r", Collections.singletonMap("id", Constants.PEER_ID));
                    sendMap.put("t", "aa".getBytes(StandardCharsets.UTF_8));
                    sendMap.put("y", "r".getBytes(StandardCharsets.UTF_8));

                ByteBuffer sendBuffer = encoder.encode(sendMap, 320);
                byte[] sendBytes = sendBuffer.array();
                socket.send(new DatagramPacket(sendBytes, sendBytes.length, packet.getAddress(), packet.getPort()));
                logger.debug("Pong : {}", Utils.dumpBencodedMapWithJackson(sendMap, new DefaultPrettyPrinter()));
                chronicler.onPing(pingCommandOptional.get());
            }

            Optional<FindNode> findNodeOptional = tryToExtractFindNode(res);
            if (findNodeOptional.isPresent()) {
                logger.info("Find_node request detected");
                chronicler.onFindNode(findNodeOptional.get());
            }

            logger.info("OK! with data: {}", binhex(packetData, true));
            String json = Utils.dumpBencodedMapWithJackson(res, new DefaultPrettyPrinter());
            logger.info("with structure : {}", json);
            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss-n"));
            try(OutputStream fos = new FileOutputStream(        destPath + "/decoded/udp-packet-" + localDateTimeString + ".dat");
                PrintWriter pw = new PrintWriter(new FileWriter(destPath + "/decoded/udp-packet-" + localDateTimeString + ".json"))) {
                fos.write(packet.getData());
                pw.write(json);
            }



        } catch (Exception ex) {
            logger.warn("!", ex);
            OutputStream fos;
            try {
                fos = new FileOutputStream(destPath + "/non-decoded/udp-packet-" + uuid);
                fos.write(packet.getData());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // {
    //  "a" : {
    //    "id" : "9ef5c2bb83effdadd3b17b6d8483b79ede139aa4"
    //  },
    //  "q" : "70696e67 ( 'ping' )",
    //  "t" : "ef14",
    //  "v" : "4c540101",
    //  "y" : "71 ( 'q' )"
    // }
    private Optional<Ping> tryToExtractPingCommand(Map<String, Object> res) {
        // java.lang.ClassCastException: class [B cannot be cast to class java.lang.String ([B and java.lang.String are in module java.base of loader 'bootstrap')
        //	at mayton.network.dhtobserver.UDPConsumer.tryToExtractPingCommand(UDPConsumer.java:164) ~[dht-observer-0.0.1-SNAPSHOT.jar:?]
        //	at mayton.network.dhtobserver.UDPConsumer.decodeCommand(UDPConsumer.java:97) [dht-observer-0.0.1-SNAPSHOT.jar:?]
        //	at mayton.network.dhtobserver.UDPConsumer.run(UDPConsumer.java:72) [dht-observer-0.0.1-SNAPSHOT.jar:?]
        //	at java.lang.Thread.run(Thread.java:834) [?:?]
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("get_peers"))) {
            Map<String, Object> a = (Map<String, Object>) res.get("a");
            return Optional.of(new Ping(Hex.encodeHexString((byte[]) a.get("id"))));
        } else {
            return Optional.empty();
        }
    }

    // {
    //  "a" : {
    //    "id" : "bb97c5623acdb44a15c088067df3ed19ba1c13d5",
    //    "target" : "bb97c5623acdb44a15c088067df3ed19ba1c13d5"
    //  },
    //  "q" : "66696e645f6e6f6465 ( 'find_node' )",
    //  "t" : "716e0000",
    //  "v" : "5554adce",
    //  "y" : "71 ( 'q' )"
    //}
    private Optional<FindNode> tryToExtractFindNode(Map<String, Object> res) {
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("find_node"))) {
            Map<String, Object> a = (Map<String, Object>) res.get("a");
            String id = Hex.encodeHexString(((byte[]) a.get("id")));
            String target = Hex.encodeHexString(((byte[]) a.get("target")));
            return Optional.of(new FindNode(id, target));
        } else {
            return Optional.empty();
        }
    }

    @Deprecated
    private boolean isGetPeers(Map<String, Object> res) {
        return res.containsKey("q") && (new String((byte[]) res.get("q")).equals("get_peers"));
    }

    @Deprecated
    private boolean isFindNode(Map<String, Object> res) {
        return res.containsKey("q") && (new String((byte[]) res.get("q")).equals("find_node"));
    }

    @Deprecated
    private boolean isPing(Map<String, Object> res) {
        return res.containsKey("q") && (new String((byte[]) res.get("q")).equals("ping"));
    }
}
