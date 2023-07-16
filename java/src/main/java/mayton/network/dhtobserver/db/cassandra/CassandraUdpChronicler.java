package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.google.inject.Inject;
import mayton.network.dhtobserver.db.UDPReceiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@Deprecated
public class CassandraUdpChronicler extends CassandraConnectionComponent implements UDPReceiver {

    @Inject
    public void init() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
    }

    @Override
    public void onReceiveUdp(@NotNull String hostIpv4, @Range(from = 0, to = 65535) int port) {
        sessionAction("UPDATE port_stats SET hits = hits + 1 WHERE port = ?", port);
    }
}
