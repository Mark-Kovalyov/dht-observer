package mayton.network.dhtobserver.dht;

import mayton.network.dhtobserver.geo.GeoRecord;

import javax.annotation.concurrent.Immutable;
import java.net.InetAddress;
import java.util.Optional;

@Immutable
public class Ping extends DhtEvent {

    private final String id;

    public Ping(String id, InetAddress inetAddress, int port, Optional<GeoRecord> optionalGeoRecord) {
        super(optionalGeoRecord, inetAddress, port);
        this.id = id;
    }

    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Ping{" +
                "id='" + id + '\'' +
                '}';
    }
}
