package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.google.inject.Inject;
import mayton.network.dhtobserver.Reporter;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CassandraReporter implements Reporter {

    private CqlSession session;

    private String keyspace = "dhtspace";

    @Inject
    public void init() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
    }

    @NotNull
    @Override
    public List<String> knownPeers() {
        ResultSet res = session.execute("select last_update_time,host from known_peers where seq=1 order by last_update_time desc");
        List<String> peers = new ArrayList<>();
        for(Row row : res) {
            peers.add(row.getString("host"));
        }
        return peers;
    }

    @Override
    public void close() {
        session.close();
    }


}
