package mayton.network.dhtobserver;

import mayton.network.dhtobserver.security.BannedIpRange;

import java.util.Optional;

public interface IpFilter {

    Optional<BannedIpRange> inRange(String ipv4);

}
