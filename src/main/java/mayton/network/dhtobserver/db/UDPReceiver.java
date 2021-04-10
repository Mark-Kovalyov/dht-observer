package mayton.network.dhtobserver.db;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import dagger.Component;
import mayton.network.dhtobserver.DhtObserverModule;
import org.jetbrains.annotations.Range;

@Singleton
@Component(modules = DhtObserverModule.class)
public interface UDPReceiver {

    void onReceiveUdp(@Nonnull String hostIpv4, @Range(from = 0, to = 65535) int port);

}
