package mayton.network.dhtobserver.db;

import mayton.network.dhtobserver.security.BannedIpRange;
import org.jetbrains.annotations.Range;

import java.util.Optional;

public interface IpFilter {

    Optional<BannedIpRange> inRange(@Range(from = 0, to = Integer.MAX_VALUE) long ipv4);

}
