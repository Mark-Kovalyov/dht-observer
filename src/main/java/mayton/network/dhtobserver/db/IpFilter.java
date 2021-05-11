package mayton.network.dhtobserver.db;

import dagger.Component;
import mayton.network.dhtobserver.DhtObserverModule;
import mayton.network.dhtobserver.security.BannedIpRange;
import org.jetbrains.annotations.Range;

import javax.inject.Singleton;
import java.util.Optional;


public interface IpFilter {

    Optional<BannedIpRange> inRange(@Range(from = 0, to = Integer.MAX_VALUE) long ipv4);

}
