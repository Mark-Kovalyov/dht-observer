package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.google.inject.Inject;
import mayton.network.dhtobserver.DhtObserverApplication;
import mayton.network.dhtobserver.db.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class CassandraReporter implements Reporter {

    static Logger logger = LogManager.getLogger(DhtObserverApplication.class);

    private CqlSession session;

    private String keyspace = "dhtspace";

    @Inject
    public void init() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
    }

    @NotNull
    @Override
    public List<String> knownPeers() {


        List<String> peers = new ArrayList<>();
        peers.add("118.69.233.218");
        peers.add("46.53.250.51");
        /*ResultSet res = session.execute("select last_update_time,host from known_peers where seq=1 order by last_update_time desc limit 10");
        for(Row row : res) {
            String host = row.getString("host");
            logger.debug("host = {}", host);
            peers.add(host);
        }
        logger.debug("overall : {} items", peers.size());*/
        return peers;
    }

    @Override
    public void close() {
        session.close();
    }


}
