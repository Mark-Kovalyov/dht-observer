package mayton.network.dhtobserver;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import mayton.network.dhtobserver.db.cassandra.CassandraChronicler;
import mayton.network.dhtobserver.db.pg.PGChronicler;
import mayton.network.dhtobserver.geo.GeoDbImpl;

public class DhtObserverModule extends AbstractModule {

    // OMG! This is so stuped to imlement singleton for Guice!
    public static DhtObserverModule dhtObserverModule = new DhtObserverModule();

    private DhtObserverModule() {}

    @Override
    protected void configure() {
        bind(Chronicler.class)
                //.annotatedWith(Names.named("CassandraChronicler"))
                .to(CassandraChronicler.class)
                .in(Scopes.SINGLETON);
        bind(Chronicler.class)
                .annotatedWith(Names.named("PG"))
                .to(PGChronicler.class)
                .in(Scopes.SINGLETON);
        bind(GeoDb.class)
                .to(GeoDbImpl.class)
                .in(Scopes.SINGLETON);
    }
}
