package mayton.network.dhtobserver.dht;

import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.geo.GeoRecord;
import java.net.Inet4Address;
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

    public String getHostAndPort() {
        if (inetAddress instanceof Inet4Address) {
            return NetworkUtils.formatIpV4((Inet4Address) inetAddress) + ":" + port;
        } else {
            return "[IPV6]:" + port;
        }

    }
}
