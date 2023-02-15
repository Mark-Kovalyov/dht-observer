package mayton.network.dhtobserver.geo;

import org.jetbrains.annotations.Range;
import java.util.Optional;

public interface GeoDb {

    Optional<GeoRecord> findFirst(long ipv4);

}
