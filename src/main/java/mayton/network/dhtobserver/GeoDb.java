package mayton.network.dhtobserver;

import dagger.Component;
import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.Range;

import javax.inject.Singleton;
import java.util.Optional;

public interface GeoDb {

    Optional<GeoRecord> findFirst(@Range(from = 0, to = Integer.MAX_VALUE) long ipv4);

}
