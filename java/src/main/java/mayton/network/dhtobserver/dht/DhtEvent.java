package mayton.network.dhtobserver.dht;

import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.Range;

import javax.validation.constraints.NotNull;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Optional;

public abstract class DhtEvent {

    private Optional<GeoRecord> geoRecord;

    private InetAddress inetAddress;

    private int port;

    public DhtEvent(Optional<GeoRecord> geoRecord, @NotNull InetAddress inetAddress, @Range(from = 0, to = 65535) int port) {
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

    public String getHostAndPort() {
        return NetworkUtils.formatIpV4((Inet4Address) inetAddress) + ":" + port;
    }
}
