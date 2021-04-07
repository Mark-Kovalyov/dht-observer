package mayton.network.dhtobserver;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import mayton.network.NetworkUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;
import the8472.bencode.BDecoder;
import the8472.bencode.BEncoder;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

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
public class DhtGetPeers {

    static final int DEFAULT_PORT = 51413;

    public static void main(String[] args) throws DecoderException {

        Thread t = new Thread(() -> {
            BDecoder bDecoder = new BDecoder();
            System.out.println("[1]");
            try (DatagramSocket socket = new DatagramSocket(DEFAULT_PORT)) {
                System.out.println("[2]");
                socket.setReuseAddress(true);
                byte[] buf = new byte[320]; // WTF?
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (!Thread.currentThread().isInterrupted()) {
                    socket.receive(packet);
                    System.out.printf("Receive udp packet : %s\n", packet.getAddress().toString());
                    try {
                        Map<String, Object> res = bDecoder.decode(ByteBuffer.wrap(packet.getData()));
                        System.out.printf("Receive udp packet : %s", packet.getAddress().toString());
                        // If a.info_hash == SenderId then accept
                        if (res.containsKey("t") && (res.get("t")).equals("aa".getBytes(StandardCharsets.UTF_8))) {
                            System.out.println("Bencoded responce : " + Utils.dumpBencodedMapWithJackson(res, new DefaultPrettyPrinter()));
                        } else {
                            System.out.println("[2.2]");
                        }
                    } catch (Exception ex) {
                        System.out.printf("Ignored unknown packet : %s\n", packet.getAddress().toString());
                    }
                }
                //InetAddress address = packet.getAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        t.start();

        Map<String, Object> sendMap = prepareDhtGetPeersCommand("8573195880de48268c84cd58bcc5a19b".getBytes(StandardCharsets.UTF_8));

        String hosts[] = {
                "212.66.34.3", "92.249.115.215"
        };

        try (DatagramSocket socket = new DatagramSocket()) {
            BEncoder encoder = new BEncoder();
            for(String host : hosts) {
                ByteBuffer sendBuffer = encoder.encode(sendMap, 320);
                byte[] sendBytes = sendBuffer.array();
                // TODO: Finish
                socket.send(new DatagramPacket(sendBytes, sendBytes.length,
                        InetAddress.getByAddress(NetworkUtils.toByteArray(host)), DEFAULT_PORT));
                System.out.printf("Sent to the host %s, UDP body : %s", host, Hex.encodeHexString(sendBytes));
                System.out.println("Bencoded : " + Utils.dumpBencodedMapWithJackson(sendMap, new DefaultPrettyPrinter()));
                System.out.println("Waiting pause");
                Thread.sleep(3000);
            }
            System.out.println("Waiting for receive ... ");
            t.join();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    // get_peers Query = {"t":"aa", "y":"q", "q":"get_peers", "a": {"id":"abcdefghij0123456789", "info_hash":"mnopqrstuvwxyz123456"}}
    @NotNull
    private static Map<String, Object> prepareDhtGetPeersCommand(byte[] sender_node_id) throws DecoderException {
        Map<String, Object> sendMap = new TreeMap<>();
        sendMap.put("t", "aa".getBytes(StandardCharsets.UTF_8));
        sendMap.put("y", "q".getBytes(StandardCharsets.UTF_8));
        sendMap.put("q", "get_peers".getBytes(StandardCharsets.UTF_8));
        sendMap.put("a", new TreeMap() {{
            put("id", sender_node_id);
            put("info_hash", Utils.generateRandomToken());
        }});

        // TODO: What is it?

        return sendMap;
    }

}
