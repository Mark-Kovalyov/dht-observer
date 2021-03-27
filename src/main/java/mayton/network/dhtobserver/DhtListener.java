package mayton.network.dhtobserver;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DhtListener implements Runnable, DhtListenerMBean {

    private Logger logger;

    private BlockingQueue<Triple<byte[], InetAddress, Integer>> udpPackets;

    private int port;
    private String threadName;

    private String shortCode; // Thiss is need for logging

    private Thread udpConsumerThread;

    public DhtListener(String threadName, int port, String shortCode) {
        this.port = port;
        this.threadName = threadName + "/" + port;
        this.shortCode = shortCode;
        ThreadContext.put("shortCode", shortCode);
        logger = LogManager.getLogger("dhtlisteners." + shortCode);
        ThreadContext.clearAll();
        udpPackets = new ArrayBlockingQueue<>(1000);
        udpConsumerThread = new Thread(new UDPConsumer(udpPackets, threadName, shortCode));
        udpConsumerThread.start();
    }

    @Override
    public void run() {
        ThreadContext.put("shortCode", shortCode);
        logger.info("Started {} listener, threadId = {}", threadName, Thread.currentThread().getId());
        try(DatagramSocket socket = new DatagramSocket(port)) {
            socket.setReuseAddress(true);
            byte[] buf = new byte[320]; // WTF?
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                logger.trace("receive udp packet : {}", packet.getAddress().toString());
                udpPackets.put(Triple.of(Arrays.copyOf(buf, buf.length), address, port));
            }
            udpConsumerThread.interrupt();
            logger.info("Interrupted!");
        } catch (IOException | InterruptedException e) {
            logger.error(e);
        } finally {
            ThreadContext.clearAll();
        }
    }

    public void askStop() {
        logger.info("Received 'askStop' for threadId = {}", Thread.currentThread().getId());
        Thread.currentThread().interrupt();
    }

    @Override
    public int getReceivedPackets() {
        return 0;
    }

    @Override
    public int getSentPackets() {
        return 0;
    }
}
