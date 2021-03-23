package mayton.network.dhtobserver.dht;

import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.net.InetAddress;
import java.util.Optional;

@Immutable
public class Ping extends DhtEvent {

    private final String id;

    private final String lastHostAndPort;

    public Ping(String id, String lastHostAndPort, Optional<GeoRecord> optionalGeoRecord, InetAddress inetAddress, int port) {
        super(optionalGeoRecord, inetAddress, port);
        this.id = id;
        this.lastHostAndPort = lastHostAndPort;
    }

    public String getId() {
        return id;
    }

    public String getLastHostAndPort() {
        return lastHostAndPort;
    }

    @Override
    public String toString() {
        return "Ping{" +
                "id='" + id + '\'' +
                ", lastHostAndPort='" + lastHostAndPort + '\'' +
                '}';
    }
}
