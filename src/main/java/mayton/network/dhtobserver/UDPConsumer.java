package mayton.network.dhtobserver;

import mayton.network.dhtobserver.jfr.DhtParseEvent;
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
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static mayton.network.dhtobserver.Utils.binhex;

public class UDPConsumer implements Runnable {

    private AtomicInteger packetsReceived = new AtomicInteger(0);

    private Logger logger = LogManager.getLogger(UDPConsumer.class);

    private BlockingQueue<Triple<byte[], InetAddress, Integer>> udpPackets;

    private String threadName;

    private String shortCode; // Thiss is need for logging

    public UDPConsumer(BlockingQueue<Triple<byte[], InetAddress, Integer>> udpPackets, String threadName, String shortCode) {
        this.udpPackets = udpPackets;
        this.threadName = threadName;
        this.shortCode = shortCode;
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

            if (isPing(res)) {
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
                logger.debug("Pong : {}", Utils.dumpBencodedMapWithJackson(sendMap));
            }
            if (isFindNode(res)) {
                logger.info("Find_node request detected");

            }

            if (isGetPeers(res)) {
                logger.info("Get_peers request detected");

            }



            logger.info("OK! with data: {}", binhex(packetData, true));
            String json = Utils.dumpBencodedMapWithJackson(res);
            logger.info("with structure : {}", json);

            try(OutputStream fos = new FileOutputStream("out/decoded/udp-packet-" + uuid);
                PrintWriter pw = new PrintWriter(new FileWriter("out/decoded/udp-packet-" + uuid + ".json"))) {
                fos.write(packet.getData());
                pw.write(json);
            }

        } catch (Exception ex) {
            logger.warn("!", ex);
            OutputStream fos = null;
            try {
                fos = new FileOutputStream("out/non-decoded/udp-packet-" + uuid);
                fos.write(packet.getData());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isGetPeers(Map<String, Object> res) {
        return res.containsKey("q") && (new String((byte[]) res.get("q")).equals("get_peers"));
    }

    private boolean isFindNode(Map<String, Object> res) {
        return res.containsKey("q") && (new String((byte[]) res.get("q")).equals("find_node"));
    }

    private boolean isPing(Map<String, Object> res) {
        return res.containsKey("q") && (new String((byte[]) res.get("q")).equals("ping"));
    }
}
