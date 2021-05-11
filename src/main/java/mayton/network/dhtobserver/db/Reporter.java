package mayton.network.dhtobserver.db;

import dagger.Component;
import mayton.network.dhtobserver.DhtObserverModule;
import javax.inject.Singleton;
import java.util.List;
import javax.annotation.Nonnull;

public interface Reporter extends AutoCloseable {

    @Nonnull
    List<String> knownPeers();

}
