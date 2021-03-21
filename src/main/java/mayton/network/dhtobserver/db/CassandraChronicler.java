package mayton.network.dhtobserver.db;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import com.google.inject.Inject;
import mayton.network.dhtobserver.Chronicler;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

//@Component
//@ConfigurationProperties(prefix = "cassandra")
public class CassandraChronicler implements Chronicler {

    private static Logger logger = LoggerFactory.getLogger(CassandraChronicler.class);

    private CqlSession session;

    private String keyspace = "dhtspace";

    public CassandraChronicler() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
    }

    //@PostConstruct
    public void postConstruct() {
        logger.info("postConstruct");

        logger.info("postConstruct done");
    }

    @Override
    public void onPing(@Nonnull Ping command) {
        logger.debug("onPing with command = {}", command.toString());
        try {
            PreparedStatement pst = session.prepare("update nodes_stats set pings_requests = pings_requests + 1 where node_id = ?");
            ResultSet res = session.execute(pst.bind(command.getId()));
            logger.debug("was applied = {}", res.wasApplied());
        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void onFindNode(FindNode command) {

    }

    @Override
    public void onGetPeers(GetPeers command) {

    }
}
