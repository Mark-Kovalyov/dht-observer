package mayton.network.dhtobserver.jfr;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("dhtobserver.jfr.DhtParseEvent")
@Category({"dht","algorithm"})
@Label("Parsing UDP/torrent packet")
public class DhtDecodePacketEvent extends Event {
    public String ip;
    public String command;
}
