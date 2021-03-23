package mayton.network.dhtobserver.geo;

import mayton.network.NetworkUtils;

import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

@Immutable
public final class GeoRecord {

    public static Comparator<GeoRecord> beginIpComparator = Comparator.comparingLong((GeoRecord r) -> r.beginIp);

    public static Comparator<GeoRecord> endIpComparator = Comparator.comparingLong((GeoRecord r) -> r.endIp);

    public final String country;

    public final String city;

    public final long beginIp;

    public final long endIp;

    public GeoRecord(String country, String city, long beginIp, long endIp) {
        this.country = country;
        this.city = city;
        this.beginIp = beginIp;
        this.endIp = endIp;
    }

    @Override
    public String toString() {
        return country + " / " + city + " [ " + NetworkUtils.formatIpV4(beginIp) + ".." + NetworkUtils.formatIpV4(endIp) + " ]";
    }
}
