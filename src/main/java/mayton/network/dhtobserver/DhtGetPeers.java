package mayton.network.dhtobserver;

import the8472.bencode.BEncoder;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static mayton.network.dhtobserver.Utils.generateRandomToken;

public class DhtGetPeers {

    public static void main(String[] args) {
        String hostPort = args[0];

        byte[] sender_node_id = Constants.PEER_ID.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> sendMap = new TreeMap<>();
            sendMap.put("id", sender_node_id);
            sendMap.put("token", generateRandomToken());
            sendMap.put("values", Collections.EMPTY_LIST);

        try (DatagramSocket socket = new DatagramSocket()) {
            BEncoder encoder = new BEncoder();
            ByteBuffer sendBuffer = encoder.encode(sendMap, 320);
            byte[] sendBytes = sendBuffer.array();
            // TODO: Finish
            //socket.send(new DatagramPacket(sendBytes, sendBytes.length, packet.getAddress(), packet.getPort()));
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

}
