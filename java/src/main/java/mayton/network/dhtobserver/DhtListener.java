package mayton.network.dhtobserver;

import com.github.rholder.retry.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DhtListener implements Runnable {

    private Logger logger;

    private BlockingQueue<UDPConsumerEntity> udpPackets;

    private int port;

    private String threadName;

    private String shortCode; // Thiss is need for logging

    private Thread udpConsumerThread;

    public DhtListener(int port, String shortCode) {
        this.port = port;
        this.threadName = shortCode + "/" + port;
        this.shortCode = shortCode;
        ThreadContext.put("shortCode", shortCode);
        logger = LogManager.getLogger("dhtlisteners." + shortCode);
        ThreadContext.clearAll();
        udpPackets = new ArrayBlockingQueue<>(1000);
        udpConsumerThread = new Thread(new UDPConsumer(udpPackets, threadName, shortCode));
        udpConsumerThread.start();
    }


    static class DatagramSocketCallable implements Callable<DatagramSocket> {

        private int port;

        public DatagramSocketCallable(int port) {
            this.port = port;
        }

        @SuppressWarnings("java:S2095")
        public DatagramSocket call() throws Exception {
            DatagramSocket socket = new DatagramSocket(port);
            socket.setReuseAddress(true);
            return socket;
        }
    }

    @Override
    public void run() {

        try {
            ThreadContext.put("shortCode", shortCode);

            DatagramSocketCallable datagramSocketCallable = new DatagramSocketCallable(port);

            Retryer<DatagramSocket> retryer = RetryerBuilder.<DatagramSocket>newBuilder()
                    .retryIfExceptionOfType(BindException.class)
                    .withWaitStrategy(WaitStrategies.randomWait(1000, MILLISECONDS, 7000, MILLISECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(20))
                    .withRetryListener(new RetryListener() {
                        @Override
                        public <V> void onRetry(Attempt<V> attempt) {
                            logger.warn("Attempt number : {}, delay since first attempt : {} ms",
                                    attempt.getAttemptNumber(), attempt.getDelaySinceFirstAttempt());
                        }
                    })
                    .build();

            DatagramSocket socket = retryer.call(datagramSocketCallable);
            if (socket != null) {
                logger.info("Started {} listener, threadId = {}", threadName, Thread.currentThread().getId());
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] buf = new byte[320]; // WTF?
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    InetAddress address = packet.getAddress();
                    if (logger.isTraceEnabled())
                        logger.trace("receive udp packet : {}", packet.getAddress());
                    udpPackets.put(new UDPConsumerEntity(Arrays.copyOf(buf, buf.length), address, port));
                }
                logger.info("Interrupted!");
            } else {
                logger.warn("Unable to create UDP socket listener after retryer attemts. Aborted!");
            }
        } catch (RetryException | ExecutionException | IOException e) {
            logger.error("!", e);
        } catch (InterruptedException e) {
            logger.warn("Interrupted", e);
        } finally {
            udpConsumerThread.interrupt();
            ThreadContext.clearAll();
        }
    }

    public void askStop() {
        logger.info("Received 'askStop' for threadId = {}", Thread.currentThread().getId());
        Thread.currentThread().interrupt();
    }

}
