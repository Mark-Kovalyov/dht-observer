package mayton.network.dhtobserver.jfr;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("dhtobserver.jfr.GeoEnrichmentEvent")
@Category({"dht","info"})
@Label("Lookup IP range from GeoIp db")
public class GeoEnrichmentEvent extends Event {
    public String ip;
}
