package mayton.network.dhtobserver;

import mayton.network.dhtobserver.jfr.DhtParseEvent;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import the8472.bencode.BDecoder;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
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
            if (logger.isInfoEnabled()) {
                logger.info("{} :: Received DHT UDP packet : from {}:{} ({})",
                        threadName,
                        packet.getAddress(),
                        "{}", //geoDb().decodeCountryCity(packet.getAddress().getHostAddress()),
                        packet.getPort()
                );
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
}
