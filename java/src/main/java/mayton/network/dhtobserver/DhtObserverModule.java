package mayton.network.dhtobserver;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.IpFilter;
import mayton.network.dhtobserver.db.Reporter;
import mayton.network.dhtobserver.db.cassandra.CassandraChronicler;
import mayton.network.dhtobserver.db.cassandra.CassandraReporter;
import mayton.network.dhtobserver.db.ignite.IgniteChronicler;
import mayton.network.dhtobserver.db.pg.PGChronicler;
import mayton.network.dhtobserver.geo.GeoDbCsvImpl;
import mayton.network.dhtobserver.security.IpFilterEmule;
import mayton.network.dhtobserver.sys.PidWriter;
import mayton.network.dhtobserver.sys.PidWriterLinuxImpl;

public class DhtObserverModule extends AbstractModule {

    // OMG! This is so stuped to imlement singleton for Guice-module to create singleton! Ahaha...
    public static DhtObserverModule dhtObserverModule = new DhtObserverModule();

    private DhtObserverModule() {}

    @Override
    protected void configure() {
        bind(Chronicler.class)
                .to(CassandraChronicler.class)
                .in(Scopes.SINGLETON);

        bind(Chronicler.class)
                .annotatedWith(Names.named("pg"))
                .to(PGChronicler.class)
                .in(Scopes.SINGLETON);

        bind(Chronicler.class)
                .annotatedWith(Names.named("ignite"))
                .to(IgniteChronicler.class)
                .in(Scopes.SINGLETON);

        bind(GeoDb.class)
                .to(GeoDbCsvImpl.class)
                .in(Scopes.SINGLETON);

        bind(Reporter.class)
                .to(CassandraReporter.class)
                .in(Scopes.SINGLETON);

        bind(ExecutorServiceProvider.class)
                .to(ExecutorServiceProviderImpl.class)
                .in(Scopes.SINGLETON);

        bind(IpFilter.class)
                .to(IpFilterEmule.class)
                .in(Scopes.SINGLETON);

        bind(ConfigProvider.class).to(YamlConfigProvider.class).in(Scopes.SINGLETON);

        bind(PidWriter.class).to(PidWriterLinuxImpl.class).in(Scopes.SINGLETON);
    }
}
