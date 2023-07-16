package mayton.network.dhtobserver.dht.handlers;

import mayton.network.dht.DhtDetector;
import mayton.network.dht.events.Ping;
import mayton.network.dhtobserver.LoggerHelper;
import mayton.network.dhtobserver.chain.BasicHandler;
import mayton.network.dhtobserver.geo.GeoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.*;

public class PingHandler extends BasicHandler {

    static Logger logger = LoggerFactory.getLogger("bigdata");

    public PingHandler(String description) {
        super(description);
    }

    @Override
    public boolean process(DatagramPacket datagramPacket, Optional<GeoRecord> geoRecordOptional) throws IOException {

        Map<String, Object> map = new LinkedHashMap<>();
        Optional<Ping> pingCommandOptional = DhtDetector.tryToExtractPingCommand(map, datagramPacket);
        if (pingCommandOptional.isPresent()) {
            onHandle();
            Ping ping = pingCommandOptional.get();
            logger.info(LoggerHelper.format(ping));
            // TODO: What is the stadnard response on Ping command?
            // TODO: What is the actions need to do?
            //dhtDecodePacketEvent.command = "ping";
            //MDC.put(DHT_EVENT_TYPE, Ping.class.getName());
            // ping Query = {"t":"aa", "y":"q", "q":"ping", "a":{"id":"abcdefghij0123456789"}}
            // bencoded = d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe
            //
            // Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
            // bencoded = d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re
            return true;
        } else {
            return next.process(datagramPacket, geoRecordOptional);
        }
    }


}
