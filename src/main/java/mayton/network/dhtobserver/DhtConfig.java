package mayton.network.dhtobserver;

import dagger.Component;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@Component(modules = DhtObserverModule.class)
public interface DhtConfig {

    String getParameter(@Nonnull String key);

}
