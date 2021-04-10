package mayton.network.dhtobserver;

import dagger.Component;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@Component(modules = DhtObserverModule.class)
public interface DhtActor {

    void ping(@Nonnull String nodeId);

    void announce(@Nonnull String some);

}
