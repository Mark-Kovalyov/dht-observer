package mayton.network.dhtobserver;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import mayton.network.dhtobserver.db.CassandraChronicler;

public class DhtObserverModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Chronicler.class)
                //.annotatedWith(Names.named("CassandraChronicler"))
                .to(CassandraChronicler.class)
                .in(Scopes.SINGLETON);
    }
}
