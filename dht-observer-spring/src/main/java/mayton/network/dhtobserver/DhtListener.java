package mayton.network.dhtobserver;

import bt.bencoding.BEParser;
import com.github.soulaway.beecoder.BeeCoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.util.NullOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import the8472.bencode.BDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static mayton.network.dhtobserver.Utils.binhex;

public class DhtListener implements Runnable, DhtListenerMBean {

    private Logger logger;

    private AtomicInteger packetsReceived = new AtomicInteger(0);
    private AtomicInteger packetsRejected = new AtomicInteger(0);

    private int port;
    private String threadName;

    private String shortCode; // Thiss is need for logging

    @Autowired
    private GeoDb geoDb() {
        return new GeoDb();
    }

    public DhtListener(String threadName, int port, String shortCode) {
        this.port = port;
        this.threadName = threadName + "/" + port;
        this.shortCode = shortCode;
        ThreadContext.put("shortCode", shortCode);
        logger = LogManager.getLogger("dhtlisteners." + shortCode);
        ThreadContext.clearAll();
    }

    @Override
    public void run() {
        ThreadContext.put("shortCode", shortCode);
        logger.info("Started {} listener, threadId = {}", threadName, Thread.currentThread().getId());
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(port);
            socket.setReuseAddress(true);
            byte[] buf = new byte[320]; // WTF?
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                decodeCommand(packet);
            }
            logger.info("Interrupted!");
            socket.close();
        } catch (IOException e) {
            logger.error(e);
        } finally {
            ThreadContext.clearAll();
        }
    }

    void decodeCommand(DatagramPacket packet) {
        packetsReceived.incrementAndGet();
        byte[] packetData = packet.getData();
        try{
            BDecoder decoder = new BDecoder();
            Map<String, Object> res = decoder.decode(ByteBuffer.wrap(packetData));
            logger.info("{} :: Received DHT UDP packet : from {}:{} ({})",
                    threadName,
                    packet.getAddress(),
                    geoDb().decodeCountryCity(packet.getAddress().getHostAddress()),
                    packet.getPort()
                    );

            logger.trace("OK! with data: {}", binhex(packetData, true));
            logger.trace("with structure : {}", Utils.dumpDEncodedMap(res));
        } catch (Exception ex) {
            logger.warn("!", ex);
            logger.warn("Unable to parse datagram: {}, with length = {}", binhex(packetData, true), packetData.length);
            logger.info("Trying to use atomashpolsky::BEParser");
            try {
                BEParser beParser = new BEParser(packetData);
                logger.info("OK! beParser.map = {}", beParser.readMap());
                logger.info("beParser.list = {}", beParser.readList());
            } catch (Exception ex2) {
                logger.warn("!", ex2);
                logger.warn("Unable to parse datagram: {}, with atomashpolsky::BEParser", binhex(packetData, true), packetData.length);
                logger.info("Trying to use soulway::BeeCoder");
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(NullOutputStream.getInstance());
                    BeeCoder.INSTANCE.decodeStream(new ByteArrayInputStream(packetData), objectOutputStream);
                    objectOutputStream.flush();
                    // TODO
                    objectOutputStream.close();
                    logger.info("OK!");
                } catch (IOException ex3) {
                    logger.warn("!", ex3);
                    logger.error("Unable to parse with soulway::BeeCoder");
                }
            }

        }
    }

    public void askStop() {
        logger.info("Received 'askStop' for threadId = {}", Thread.currentThread().getId());
        Thread.currentThread().interrupt();
    }

}
