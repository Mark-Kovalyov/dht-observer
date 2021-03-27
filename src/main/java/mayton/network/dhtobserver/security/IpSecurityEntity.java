package mayton.network.dhtobserver.security;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class IpSecurityEntity {

    public final long beginIp;
    public final long endIp;
    public final String comment;

    public IpSecurityEntity(long beginIp, long endIp, String comment) {
        this.beginIp = beginIp;
        this.endIp = endIp;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "IpSecurityEntity{" +
                "beginIp=" + beginIp +
                ", endIp=" + endIp +
                ", comment='" + comment + '\'' +
                '}';
    }
}
