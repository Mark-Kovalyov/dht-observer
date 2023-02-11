package mayton.network.dhtobserver;

import mayton.dht.DhtMapConverter;
import mayton.dht.MapJsonConverter;
import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.chain.Handler;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.IpFilter;
import mayton.network.dhtobserver.db.Reporter;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;
import mayton.network.dhtobserver.dht.handlers.AnnouncePeerHandler;
import mayton.network.dhtobserver.dht.handlers.FindNodeHandler;
import mayton.network.dhtobserver.dht.handlers.GetPeerHandler;
import mayton.network.dhtobserver.dht.handlers.PingHandler;
import mayton.network.dhtobserver.geo.GeoRecord;
import mayton.network.dhtobserver.jfr.DhtDecodePacketEvent;
import mayton.network.dhtobserver.security.BannedIpRange;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.Validate;
import org.apache.ignite.internal.processors.cache.persistence.tree.BPlusTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import java.io.*;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static mayton.libs.encoders.binhex.BinHexUtils.binhex;
import static mayton.network.dhtobserver.Constants.DHT_EVENT_TYPE;

public class UDPConsumer implements Runnable {

    private Chronicler chronicler;

    private Reporter reporter;

    private GeoDb geoDb;

    private AtomicInteger packetsReceived = new AtomicInteger(0);

    private Logger logger;

    private IpFilter ipFilter;

    private BlockingQueue<UDPConsumerEntity> udpPackets;

    private String threadName;

    private String shortCode; // Thiss is need for logging

    private String destPath = "/bigdata/dht-observer/out";

    private Random random = new Random();

    private ConfigProvider configProvider;

    private String nodeId;

    private Handler root;

    public UDPConsumer(BlockingQueue<UDPConsumerEntity> udpPackets, String threadName, String shortCode) {
        this.udpPackets = udpPackets;
        this.threadName = threadName;
        this.shortCode = shortCode;
        new File(destPath + "/decoded").mkdirs();
        new File(destPath + "/non-decoded").mkdirs();
        ThreadContext.put("shortCode", shortCode);
        logger = LogManager.getLogger("dhtlisteners." + shortCode);
        root = new PingHandler("");
        root.add(new FindNodeHandler(""));
        //root.add(new GetPeerHandler(""));
        //root.add(new AnnouncePeerHandler(""));
    }

    @Override
    public void run() {
        configProvider = DhtObserverApplication.injector.getInstance(ConfigProvider.class);
        chronicler = DhtObserverApplication.injector.getInstance(Chronicler.class);
        geoDb = DhtObserverApplication.injector.getInstance(GeoDb.class);
        reporter = DhtObserverApplication.injector.getInstance(Reporter.class);
        ipFilter = DhtObserverApplication.injector.getInstance(IpFilter.class);
        nodeId = configProvider.getNodeId();
        while(!Thread.currentThread().isInterrupted()) {
            try {
                logger.trace("Consume...");
                UDPConsumerEntity item = udpPackets.take();
                InetAddress ip = item.inetAddress;
                logger.trace("Generic UDP IP : {}", ip.toString());
                if (ip instanceof Inet4Address) {
                    logger.trace("IPv4. Analyzing...");
                    String ipString = NetworkUtils.formatIpV4((Inet4Address) ip);
                    logger.trace("After format IPv4 string : {}", ipString);
                    Optional<BannedIpRange> bannedIpRange = ipFilter.inRange(NetworkUtils.parseIpV4(ipString));
                    if (bannedIpRange.isEmpty()) {
                        logger.trace("Allowed!");
                        byte[] buf = item.data;
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, item.port);
                        decodeCommand(packet);
                    } else {
                        logger.warn("Banned ipv4 {} detected in {}", ip, bannedIpRange.get());
                    }
                } else {
                    logger.warn("IP address {} is non IPv4. Ignored", ip);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("", e);
            }
        }
    }

    void decodeCommand(DatagramPacket packet) {
        try {
            Optional<GeoRecord> geoRecordOptional = geoDb.findFirst(NetworkUtils.fromIpv4toLong(packet.getAddress()));
            /*if (geoRecordOptional.isEmpty()) {
                logger.warn("Hmm... unable to recognize GeoIp binding for ip = {}, long = {}", packet.getAddress(), NetworkUtils.fromIpv4toLong(packet.getAddress()));
            }*/
            root.process(packet, geoRecordOptional);
        } catch (IOException e) {
            logger.error("!", e);
        }
    }

    @SuppressWarnings("java:S2629")
    void decodeCommand2(DatagramPacket packet) {
        MapJsonConverter converter = new MapJsonConverter();
        DhtDecodePacketEvent dhtDecodePacketEvent = new DhtDecodePacketEvent();
        dhtDecodePacketEvent.begin();
        LocalDateTime localDateTime = LocalDateTime.now();
        String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss-n"));
        packetsReceived.incrementAndGet();
        byte[] packetData = packet.getData();
        dhtDecodePacketEvent.ip = String.valueOf(packet.getAddress());
        DhtMapConverter dhtMapConverter = new DhtMapConverter();
        try{
            //BDecoder decoder = new BDecoder();
            //Map<String, Object> res = decoder.decode(ByteBuffer.wrap(packetData));
            Optional<Map<String, Object>> optionalRes = dhtMapConverter.convert(packet);
            logger.info("{} :: Received DHT UDP packet : from {}:{}",
                    threadName,
                    packet.getAddress(),
                    packet.getPort()
            );

            Optional<GeoRecord> geoRecordOptional = geoDb.findFirst(NetworkUtils.fromIpv4toLong(packet.getAddress()));
            /*if (geoRecordOptional.isEmpty()) {
                logger.warn("Hmm... unable to recognize GeoIp binding for ip = {}, long = {}", packet.getAddress(), NetworkUtils.fromIpv4toLong(packet.getAddress()));
            }*/
            if (optionalRes.isPresent()) {
                Map<String, Object> res = optionalRes.get();

                    Optional<FindNode> findNodeOptional = tryToExtractFindNode(res, packet, geoRecordOptional);
                    if (findNodeOptional.isPresent()) {
                        dhtDecodePacketEvent.command = "find";
                        MDC.put(DHT_EVENT_TYPE, FindNode.class.getName());
                        logger.info("Find_node request detected");
                        chronicler.onFindNode(findNodeOptional.get());
                    } else {
                        Optional<GetPeers> getPeersOptional = extractGetPeers(res, packet, geoRecordOptional);
                        if (getPeersOptional.isPresent()) {
                            dhtDecodePacketEvent.command = "get_peers";
                            byte[] sender_node_id = (byte[]) res.get("id");
                            if (sender_node_id == null) {
                                logger.warn("Hmm.. 'geet_peers' without id?");
                            } else {
                                MDC.put(DHT_EVENT_TYPE, GetPeers.class.getName());
                                String token = UUID.randomUUID().toString(); //generateRandomToken();
                                Validate.notNull(reporter.knownPeers(), "Known peers must be not null");
                                chronicler.onGetPeers(getPeersOptional.get());
                                // response: {"id" : "<queried nodes id>", "token" :"<opaque write token>", "values" : ["<peer 1 info string>", "<peer 2 info string>"]}
                                // or: {"id" : "<queried nodes id>", "token" :"<opaque write token>", "nodes" : "<compact node info>"}
                                /*Map<String, Object> sendMap2 = new TreeMap<>();
                                sendMap2.put("id", sender_node_id);
                                sendMap2.put("token", token);
                                sendMap2.put("values", reporter.knownPeers());
                                try (DatagramSocket socket = new DatagramSocket()) {
                                    logger.debug("Pong prepare...");
                                    ByteBuffer sendBuffer2 = encoder.encode(sendMap2, 320);
                                    byte[] sendBytes2 = sendBuffer2.array();
                                    // TODO: Finish
                                    Validate.notNull(sendBytes2, "Send bytes must be non null");
                                    Validate.notNull(packet, "UDP packet is empty");
                                    Validate.notNull(packet.getAddress(), "Adress of UDP packet is empty");
                                    socket.send(new DatagramPacket(sendBytes2, sendBytes2.length, packet.getAddress(), packet.getPort()));
                                    logger.debug("Pong : {}", converter.convertPretty(sendMap2).get());
                                }*/
                            }
                        } else {
                            Optional<AnnouncePeer> optionalAnnouncePeer = extractAnnounce(res, packet, geoRecordOptional);
                            if (optionalAnnouncePeer.isPresent()) {
                                dhtDecodePacketEvent.command = "announce_peer";
                                MDC.put(DHT_EVENT_TYPE, AnnouncePeer.class.getName());
                                // announce_peers Query = {"t":"aa", "y":"q", "q":"announce_peer", "a": {"id":"abcdefghij0123456789", "implied_port": 1, "info_hash":"mnopqrstuvwxyz123456", "port": 6881, "token": "aoeusnth"}}
                                // Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
                                // TODO: Refactor
                                /*Map<String, Object> sendMap = new TreeMap<>();
                                sendMap.put("t", "aa".getBytes(StandardCharsets.UTF_8));
                                sendMap.put("y", "r".getBytes(StandardCharsets.UTF_8));
                                sendMap.put("r", "");*/
                                chronicler.onAnnouncePeer(optionalAnnouncePeer.get());
                            } else {
                                logger.warn("Unknown message type : {}", converter.convertPretty(res));
                            }
                        }
                    }


                logger.info("OK! with data: {}", binhex(packetData, true));
                String json = converter.convertPretty(res).get();
                logger.info("with structure : {}", json);
                try(OutputStream fos = new FileOutputStream(        destPath + "/decoded/udp-packet-" + localDateTimeString + ".dat");
                    PrintWriter pw = new PrintWriter(new FileWriter(destPath + "/decoded/udp-packet-" + localDateTimeString + ".json"))) {
                    fos.write(packet.getData());
                    pw.write(json);
                }
            } else {
                try (OutputStream fos = new FileOutputStream(destPath + "/non-decoded/udp-packet-" + localDateTimeString + ".dat")) {
                    fos.write(packet.getData());
                } catch (IOException e) {
                    logger.warn("!", e);
                }
            }
        } catch (Exception ex) {
            logger.error("!", ex);
        } finally {
            MDC.remove(DHT_EVENT_TYPE);
            dhtDecodePacketEvent.commit();
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
            return Optional.of(new FindNode(
                    id, target,
                    geoRecord,
                    packet.getAddress(),
                    packet.getPort()));
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
