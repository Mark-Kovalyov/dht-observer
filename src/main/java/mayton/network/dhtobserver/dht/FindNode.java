package mayton.network.dhtobserver.dht;

import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.net.InetAddress;
import java.util.Optional;

@Immutable
public class FindNode extends DhtEvent {

    private final String id;
    private final String target;
    private final String hostAndPort;
    private final Optional<GeoRecord> geoRecord;

    public FindNode(String id, String target, String hostAndPort, Optional<GeoRecord> geoRecord, InetAddress inetAddress, int port) {
        super(geoRecord, inetAddress, port);
        this.id = id;
        this.target = target;
        this.hostAndPort = hostAndPort;
        this.geoRecord = geoRecord;
    }

    public String getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "FindNode{" +
                "id='" + id + '\'' +
                ", target='" + target + '\'' +
                ", hostAndPort='" + hostAndPort + '\'' +
                '}';
    }

    public String getHostAndPort() {
        return hostAndPort;
    }

    public Optional<GeoRecord> getGeoRecord() {
        return geoRecord;
    }
}
