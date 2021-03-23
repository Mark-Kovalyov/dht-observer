package mayton.network.dhtobserver.dht;

import mayton.network.dhtobserver.geo.GeoRecord;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.util.Optional;

public abstract class DhtEvent {

    private Optional<GeoRecord> geoRecord;

    private InetAddress inetAddress;

    private int port;

    public DhtEvent(Optional<GeoRecord> geoRecord, InetAddress inetAddress, int port) {
        this.geoRecord = geoRecord;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public Optional<GeoRecord> getGeoRecord() {
        return geoRecord;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }
}
