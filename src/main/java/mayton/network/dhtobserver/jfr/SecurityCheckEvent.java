package mayton.network.dhtobserver.jfr;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("dhtobserver.jfr.SecurityCheckEvent")
@Category({"dht","security"})
@Label("Checking security IP")
public class SecurityCheckEvent extends Event {
    public String ip;
    public boolean banned;
}
