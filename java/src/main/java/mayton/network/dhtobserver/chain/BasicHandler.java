package mayton.network.dhtobserver.chain;

import com.google.inject.Key;
import mayton.network.dhtobserver.DhtObserverApplication;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.cassandra.CassandraChronicler;
import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.NotNull;

import javax.management.MBeanServer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Optional;

public class BasicHandler implements Handler {

    public Handler next;
    public int handles;
    public int passes;
    public int errors;
    public String description;
    protected Chronicler chronicler;

    public BasicHandler(String description) {
        this.description = description;
        // TODO: Get rid of hardcoded Cassandra
        Key<CassandraChronicler> key = Key.get(CassandraChronicler.class);
        chronicler = DhtObserverApplication.injector.getInstance(key);
    }

    @Override
    public boolean process(DatagramPacket datagramPacket, Optional<GeoRecord> geoRecordOptional) throws IOException {
        return false;
    }

    @Override
    public void add(@NotNull Handler next) {
        if (this.next == null) {
            this.next = next;
        } else {
            next.add(next);
        }
    }

    @Override
    public void onHandle() {
        handles++;
    }

    @Override
    public void onPass() {
        passes++;
    }

    @Override
    public void onError() {
        errors++;
    }

    @Override
    public String report() {
        return String.format("Handler: %s, Handles: %d, Passes: %d, Errors: %d", description, handles, passes, errors);
    }
}
