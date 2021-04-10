package mayton.network.dhtobserver;

import dagger.Module;
import dagger.Provides;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.IpFilter;
import mayton.network.dhtobserver.db.Reporter;
import mayton.network.dhtobserver.db.cassandra.CassandraChronicler;
import mayton.network.dhtobserver.db.cassandra.CassandraReporter;
import mayton.network.dhtobserver.db.ignite.IgniteChronicler;
import mayton.network.dhtobserver.db.pg.PGChronicler;
import mayton.network.dhtobserver.geo.GeoDbImpl;
import mayton.network.dhtobserver.security.IpFilterEmule;

import javax.inject.Singleton;

@Module
public class DhtObserverModule {

    @Provides
    @Singleton
    public Chronicler provideChronicler() {
        return new CassandraChronicler();
    }

    @Provides
    @Singleton
    public Chronicler providePgChronicler() {
        return new PGChronicler();
    }

    @Provides
    @Singleton
    public Chronicler provideIgniteChronicler() {
        return new IgniteChronicler();
    }

    @Provides
    @Singleton
    public GeoDb provideGeoDb() {
        return new GeoDbImpl();
    }

    @Provides
    @Singleton
    public Reporter provideCassandraReporter() {
        return new CassandraReporter();
    }

    @Provides
    @Singleton
    public ExecutorServiceProvider provideExecutorServiceProvider() {
        return new ExecutorServiceProviderImpl();
    }

    @Provides
    @Singleton
    public IpFilter provideIpFilter() {
        return new IpFilterEmule();
    }

    @Provides
    @Singleton
    public ConfigProvider provideYamlConfigProvider() {
        return new YamlConfigProvider();
    }

}
