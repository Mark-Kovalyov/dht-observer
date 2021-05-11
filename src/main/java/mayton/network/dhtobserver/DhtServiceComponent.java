package mayton.network.dhtobserver;

import dagger.Component;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.IpFilter;
import mayton.network.dhtobserver.db.Reporter;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DhtObserverModule.class})
public interface DhtServiceComponent {

    Chronicler provideChronicler();

    GeoDb provideGeoDb();

    Reporter provideReporter();

    ExecutorServiceProvider provideExecutorServiceProvider();

    IpFilter provideIpFilter();

    ConfigProvider provideConfigProvider();

}
