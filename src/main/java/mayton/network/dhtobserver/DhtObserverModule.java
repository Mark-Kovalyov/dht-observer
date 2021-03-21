package mayton.network.dhtobserver;

import com.google.inject.AbstractModule;
import mayton.network.dhtobserver.db.CassandraChronicler;

public class DhtObserverModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Chronicler.class).to(CassandraChronicler.class);
    }
}
