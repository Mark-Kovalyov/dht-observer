package mayton.network.dhtobserver;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;
import mayton.network.dhtobserver.geo.GeoRecord;
import mayton.network.dhtobserver.jfr.DhtParseEvent;
import mayton.network.dhtobserver.security.BannedIpRange;
import mayton.network.dhtobserver.security.IpFilterEmule;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import org.slf4j.MDC;
import the8472.bencode.BDecoder;
import the8472.bencode.BEncoder;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static mayton.network.dhtobserver.Constants.DHT_EVEN_TYPE;
import static mayton.network.dhtobserver.Utils.binhex;

public class UDPConsumer implements Runnable {

    private Chronicler chronicler;

    private Reporter reporter;

    private GeoDb geoDb;

    private AtomicInteger packetsReceived = new AtomicInteger(0);

    private Logger logger;

    private IpFilter ipFilter;

    private BlockingQueue<Triple<byte[], InetAddress, Integer>> udpPackets;

    private String threadName;

    private String shortCode; // Thiss is need for logging

    private String destPath = "/bigdata/dht-observer/out";

    private Random random = new Random();

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
        chronicler = DhtObserverApplication.injector.getInstance(Chronicler.class);
        geoDb = DhtObserverApplication.injector.getInstance(GeoDb.class);
        reporter = DhtObserverApplication.injector.getInstance(Reporter.class);
        ipFilter = DhtObserverApplication.injector.getInstance(IpFilter.class);
        while(!Thread.currentThread().isInterrupted()) {
            try {
                logger.trace("Consume...");
                DhtParseEvent dhtParseEvent = new DhtParseEvent();
                dhtParseEvent.shortCode = shortCode;
                dhtParseEvent.begin();
                Triple<byte[], InetAddress, Integer> item = udpPackets.take();
                InetAddress ip = item.getMiddle();
                logger.trace("Generic UDP IP : {}", ip.toString());
                if (ip instanceof Inet4Address) {
                    logger.trace("IPv4. Analyzing...");
                    String ipString = NetworkUtils.formatIpV4((Inet4Address) ip);
                    logger.trace("After format IPv4 string : {}", ipString);
                    Optional<BannedIpRange> bannedIpRange = ipFilter.inRange(ipString);
                    if (bannedIpRange.isEmpty()) {
                        logger.trace("Allowed!");
                        byte[] buf = item.getLeft();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, item.getRight());
                        decodeCommand(packet);
                    } else {
                        logger.warn("Banned ipv4 {} detected in {}", ip, bannedIpRange.get());
                    }
                } else {
                    logger.warn("IP address {} is non IPv4. Ignored", ip);
                }
                dhtParseEvent.end();
                dhtParseEvent.commit();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("", e);
            }
        }
    }

    String generateRandomToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) sb.append('a' + random.nextInt('z' - 'a'));
        return sb.toString();
    }

    @SuppressWarnings("java:S2629")
    void decodeCommand(DatagramPacket packet) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss-n"));
        packetsReceived.incrementAndGet();
        byte[] packetData = packet.getData();
        try{
            BDecoder decoder = new BDecoder();
            Map<String, Object> res = decoder.decode(ByteBuffer.wrap(packetData));
            logger.info("{} :: Received DHT UDP packet : from {}:{}",
                    threadName,
                    packet.getAddress(),
                    packet.getPort()
            );
            Optional<GeoRecord> geoRecordOptional = geoDb.findFirst(NetworkUtils.fromIpv4toLong(packet.getAddress()));
            Optional<Ping> pingCommandOptional = tryToExtractPingCommand(res, packet, geoRecordOptional);
            if (pingCommandOptional.isPresent()) {
                MDC.put(DHT_EVEN_TYPE, Ping.class.getName());
                // ping Query = {"t":"aa", "y":"q", "q":"ping", "a":{"id":"abcdefghij0123456789"}}
                // bencoded = d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe
                //
                // Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
                // bencoded = d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re
                logger.info("Ping detected");
                try(DatagramSocket socket = new DatagramSocket()) {
                    BEncoder encoder = new BEncoder();
                    Map<String, Object> sendMap = new TreeMap<>();
                        sendMap.put("r", Collections.singletonMap("id", Constants.PEER_ID));
                        sendMap.put("t", "aa".getBytes(StandardCharsets.UTF_8));
                        sendMap.put("y", "r".getBytes(StandardCharsets.UTF_8));

                    // TODO: Refactor code duplications
                    ByteBuffer sendBuffer = encoder.encode(sendMap, 320);
                    byte[] sendBytes = sendBuffer.array();
                    socket.send(new DatagramPacket(sendBytes, sendBytes.length, packet.getAddress(), packet.getPort()));
                    logger.debug("Pong : {}", Utils.dumpBencodedMapWithJackson(sendMap, new DefaultPrettyPrinter()));
                }
                chronicler.onPing(pingCommandOptional.get());
            } else {
                Optional<FindNode> findNodeOptional = tryToExtractFindNode(res, packet, geoRecordOptional);
                if (findNodeOptional.isPresent()) {
                    MDC.put(DHT_EVEN_TYPE, FindNode.class.getName());
                    logger.info("Find_node request detected");
                    chronicler.onFindNode(findNodeOptional.get());
                } else {
                    Optional<GetPeers> getPeersOptional = extractGetPeers(res, packet, geoRecordOptional);
                    if (getPeersOptional.isPresent()) {
                        MDC.put(DHT_EVEN_TYPE, GetPeers.class.getName());
                        chronicler.onGetPeers(getPeersOptional.get());
                        // response: {"id" : "<queried nodes id>", "token" :"<opaque write token>", "values" : ["<peer 1 info string>", "<peer 2 info string>"]}
                        // or: {"id" : "<queried nodes id>", "token" :"<opaque write token>", "nodes" : "<compact node info>"}
                        byte[] sender_node_id = (byte[]) res.get("id");
                        Map<String, Object> sendMap = new TreeMap<>();
                            sendMap.put("id", sender_node_id);
                            sendMap.put("token", generateRandomToken());
                            sendMap.put("values", reporter.knownPeers().stream().limit(10).collect(Collectors.toList()));
                            
                        try (DatagramSocket socket = new DatagramSocket()) {
                            BEncoder encoder = new BEncoder();
                            ByteBuffer sendBuffer = encoder.encode(sendMap, 320);
                            byte[] sendBytes = sendBuffer.array();
                            // TODO: Finish
                            //socket.send(new DatagramPacket(sendBytes, sendBytes.length, packet.getAddress(), packet.getPort()));
                        }
                        logger.debug("Pong : {}", Utils.dumpBencodedMapWithJackson(sendMap, new DefaultPrettyPrinter()));
                    } else {
                        Optional<AnnouncePeer> optionalAnnouncePeer = extractAnnounce(res, packet, geoRecordOptional);
                        if (optionalAnnouncePeer.isPresent()) {
                            MDC.put(DHT_EVEN_TYPE, AnnouncePeer.class.getName());
                            // announce_peers Query = {"t":"aa", "y":"q", "q":"announce_peer", "a": {"id":"abcdefghij0123456789", "implied_port": 1, "info_hash":"mnopqrstuvwxyz123456", "port": 6881, "token": "aoeusnth"}}
                            // Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
                            // TODO: Refactor
                            Map<String, Object> sendMap = new TreeMap<>();
                                sendMap.put("t", "aa".getBytes(StandardCharsets.UTF_8));
                                sendMap.put("y", "r".getBytes(StandardCharsets.UTF_8));
                                sendMap.put("r", "");
                            chronicler.onAnnouncePeer(optionalAnnouncePeer.get());
                        } else {
                            logger.warn("Unknown message type : {}", Utils.dumpBencodedMapWithJackson(res, new DefaultPrettyPrinter()));
                        }
                    }
                }

            }

            logger.info("OK! with data: {}", binhex(packetData, true));
            String json = Utils.dumpBencodedMapWithJackson(res, new DefaultPrettyPrinter());
            logger.info("with structure : {}", json);

            try(OutputStream fos = new FileOutputStream(        destPath + "/decoded/udp-packet-" + localDateTimeString + ".dat");
                PrintWriter pw = new PrintWriter(new FileWriter(destPath + "/decoded/udp-packet-" + localDateTimeString + ".json"))) {
                fos.write(packet.getData());
                pw.write(json);
            }

        } catch (Exception ex) {
            logger.warn("!", ex);
            try (OutputStream fos = new FileOutputStream(destPath + "/non-decoded/udp-packet-" + localDateTimeString + ".dat")) {
                fos.write(packet.getData());
            } catch (IOException e) {
                logger.warn("!", e);
            }
        } finally {
            MDC.remove(DHT_EVEN_TYPE);
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

    // {
    //  "a" : {
    //    "id" : "16b4dd510541214124bc6c953d162a8206b28d8a"
    //  },
    //  "q" : "70696e67 ( 'ping' )",
    //  "t" : "0e6bf8e3",
    //  "v" : "55547021 ( 'UTp!' )",
    //  "y" : "71 ( 'q' )"
    //}
    private Optional<Ping> tryToExtractPingCommand(Map<String, Object> res, DatagramPacket packet, Optional<GeoRecord> optionalGeoRecord) {
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("get_peers"))) {
            Map<String, Object> a = (Map<String, Object>) res.get("a");
            return Optional.of(new Ping(
                    Hex.encodeHexString((byte[]) a.get("id")),
                    packet.getAddress().getHostAddress() + ":" + packet.getPort(),
                    optionalGeoRecord, packet.getAddress(), packet.getPort()));
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
    private Optional<FindNode> tryToExtractFindNode(Map<String, Object> res, DatagramPacket packet, Optional<GeoRecord> geoRecord) {
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("find_node"))) {
            Map<String, Object> a = (Map<String, Object>) res.get("a");
            String id = Hex.encodeHexString(((byte[]) a.get("id")));
            String target = Hex.encodeHexString(((byte[]) a.get("target")));
            return Optional.of(new FindNode(id, target,
                    packet.getAddress().getHostAddress() + ":" + packet.getPort(),
                    geoRecord, packet.getAddress(), packet.getPort()));
        } else {
            return Optional.empty();
        }
    }

    // {
    //  "a" : {
    //    "id" : "46e25aba771dcae058ae69d331f07b1cf2f0b20d",
    //    "info_hash" : "46e25bc840732becba9724001b607740c4fc83b7"
    //  },
    //  "q" : "6765745f7065657273 ( 'get_peers' )",
    //  "t" : "3ea7",
    //  "v" : "4c540126",
    //  "y" : "71 ( 'q' )"
    //}
    private Optional<GetPeers> extractGetPeers(Map<String, Object> res, DatagramPacket packet, Optional<GeoRecord> geoRecord) {
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("get_peers"))) {
            Map<String, Object> a = (Map<String, Object>) res.get("a");
            String id = Hex.encodeHexString(((byte[]) a.get("id")));
            String infoHash = Hex.encodeHexString(((byte[]) a.get("info_hash")));
            return Optional.of(new GetPeers(id, infoHash, geoRecord, packet.getAddress(), packet.getPort()));
        } else {
            return Optional.empty();
        }
    }

    // {
    //  "a" : {
    //    "id" : "842ca6cee2344eb7af82a21687d918615f9451e6",
    //    "info_hash" : "c3c66bcd081558d1297fd65e1803988f54b82935",
    //    "token" : "dbd47bcfa0715529"
    //  },
    //  "q" : "616e6e6f756e63655f70656572 ( 'announce_peer' )",
    //  "t" : "1b6e0900",
    //  "y" : "71 ( 'q' )"
    //}
    private Optional<AnnouncePeer> extractAnnounce(Map<String, Object> res, DatagramPacket packet, Optional<GeoRecord> geoRecordOptional) {
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("announce_peer"))) {
            Map<String, Object> a = (Map<String, Object>) res.get("a");
            String id = Hex.encodeHexString(((byte[]) a.get("id")));
            String infoHash = Hex.encodeHexString(((byte[]) a.get("info_hash")));
            String token = Hex.encodeHexString(((byte[]) a.get("token")));
            return Optional.of(new AnnouncePeer(id, infoHash, token, packet.getPort(), geoRecordOptional, packet.getAddress()));
        } else {
            return Optional.empty();
        }
    }

}
