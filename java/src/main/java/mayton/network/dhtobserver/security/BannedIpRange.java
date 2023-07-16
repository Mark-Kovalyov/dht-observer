package mayton.network.dhtobserver.security;

import mayton.network.NetworkUtils;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class BannedIpRange {

    public final long beginIp;
    public final long endIp;
    public final String comment;

    public BannedIpRange(long beginIp, long endIp, String comment) {
        this.beginIp = beginIp;
        this.endIp = endIp;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "BannedIpRange{" +
                "beginIp=" + NetworkUtils.formatIpV4(beginIp) +
                ", endIp=" + NetworkUtils.formatIpV4(endIp) +
                ", comment='" + comment + '\'' +
                '}';
    }
}
