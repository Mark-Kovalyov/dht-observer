package mayton.network.dhtobserver.jfr;

import jdk.jfr.Event;
import jdk.jfr.Name;

@Name("dhtobserver.jfr.DhtParseEvent")
public class DhtParseEvent extends Event {
    public String shortCode;
}
