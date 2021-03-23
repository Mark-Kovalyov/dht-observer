package mayton.network.dhtobserver.db;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import com.datastax.oss.driver.api.core.cql.Statement;
import com.google.inject.Inject;
import mayton.network.dhtobserver.Chronicler;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public class CassandraChronicler implements Chronicler {

    private static Logger logger = LoggerFactory.getLogger(CassandraChronicler.class);

    private CqlSession session;

    private String keyspace = "dhtspace";

    public CassandraChronicler() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
    }

    @Inject
    public void init() {
        logger.info("postConstruct");
        logger.info("postConstruct done");
    }

    private boolean sessionAction(String cqlCommand, Object... arguments) {
        PreparedStatement pst = session.prepare(cqlCommand);
        ResultSet res = session.execute(pst.bind(arguments));
        if (!res.wasApplied()) {
            logger.warn("Warning. Something going wrong during {}", cqlCommand);
        }
        return res.wasApplied();
    }

    @Override
    public void onPing(@Nonnull Ping command) {
        logger.debug("onPing with command = {}", command.toString());
        try {
            sessionAction("update nodes_stats set pings_requests = pings_requests + 1 where node_id = ?", command.getId());
            sessionAction("update nodes_hosts set last_ip_port = ?, last_update_time = toTimeStamp(now()) where node_id = ?", command.getLastHostAndPort(), command.getId());
        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void onFindNode(FindNode command) {
        logger.debug("onFindNode with command = {}", command.toString());
        try {
            sessionAction("update nodes_stats set find_nodes_requests = find_nodes_requests + 1 where node_id = ?", command.getId());
            sessionAction("update targets set x = 1 where target_id = ? and node_id = ?", command.getTarget(), command.getId());
            // TODO: Fix 2021-03-23 00:00:00.000000+0000 empty Time
            sessionAction("update nodes_hosts set last_ip_port = ?, last_update_time = toTimeStamp(now()) where node_id = ?", command.getHostAndPort(), command.getId());
        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void onGetPeers(GetPeers command) {

    }
}
