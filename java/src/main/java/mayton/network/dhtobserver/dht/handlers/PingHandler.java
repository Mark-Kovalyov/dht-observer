package mayton.network.dhtobserver.dht.handlers;

import mayton.network.dhtobserver.chain.BasicHandler;
import mayton.network.dhtobserver.dht.Ping;
import mayton.network.dhtobserver.geo.GeoRecord;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static mayton.network.dhtobserver.Constants.DHT_EVENT_TYPE;

public class PingHandler extends BasicHandler {

    static Logger logger = LoggerFactory.getLogger(PingHandler.class);

    private Optional<Ping> tryToExtractPingCommand(Map<String, Object> res, DatagramPacket packet, Optional<GeoRecord> optionalGeoRecord) {
        if (res.containsKey("q") && (new String((byte[]) res.get("q")).equals("ping"))) {
            if (res.containsKey("a")) {
                Map<String, Object> a = (Map<String, Object>) res.get("a");
                if (a.containsKey("id")) {
                    return Optional.of(new Ping(
                            Hex.encodeHexString((byte[]) a.get("id")),
                            packet.getAddress(),
                            packet.getPort(),
                            optionalGeoRecord));
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public PingHandler(String description) {
        super(description);
    }

    @Override
    public boolean process(DatagramPacket datagramPacket, Optional<GeoRecord> geoRecordOptional) throws IOException {
        logger.info("process");
        Map<String, Object> map = new LinkedHashMap<>();
        Optional<Ping> pingCommandOptional = tryToExtractPingCommand(map, datagramPacket, geoRecordOptional);
        if (pingCommandOptional.isPresent()) {
            onHandle();
            //dhtDecodePacketEvent.command = "ping";
            MDC.put(DHT_EVENT_TYPE, Ping.class.getName());
            // ping Query = {"t":"aa", "y":"q", "q":"ping", "a":{"id":"abcdefghij0123456789"}}
            // bencoded = d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe
            //
            // Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
            // bencoded = d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re
            logger.info("Ping detected");
                    /*try (DatagramSocket socket = new DatagramSocket()) {

                        Map<String, Object> sendMap = new TreeMap<>();
                        sendMap.put("r", Collections.singletonMap("id", nodeId));
                        sendMap.put("t", "aa".getBytes(StandardCharsets.UTF_8));
                        sendMap.put("y", "r".getBytes(StandardCharsets.UTF_8));

                        // TODO: Refactor code duplications
                        ByteBuffer sendBuffer = encoder.encode(sendMap, 320);
                        byte[] sendBytes = sendBuffer.array();
                        socket.send(new DatagramPacket(sendBytes, sendBytes.length, packet.getAddress(), packet.getPort()));
                        logger.debug("Pong : {}", converter.convertPretty(sendMap).get());
                    }*/
            chronicler.onPing(pingCommandOptional.get());
            return true;
        } else {
            return next.process(datagramPacket, geoRecordOptional);
        }
    }


}
