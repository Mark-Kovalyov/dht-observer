package mayton.network.dhtobserver;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import mayton.network.dhtobserver.db.cassandra.CassandraChronicler;
import mayton.network.dhtobserver.db.cassandra.CassandraReporter;
import mayton.network.dhtobserver.db.pg.PGChronicler;
import mayton.network.dhtobserver.geo.GeoDbImpl;

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

        bind(GeoDb.class)
                .to(GeoDbImpl.class)
                .in(Scopes.SINGLETON);

        bind(Reporter.class)
                .to(CassandraReporter.class)
                .in(Scopes.SINGLETON);
    }
}
