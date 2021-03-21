package mayton.network.dhtobserver.db;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import mayton.network.dhtobserver.Chronicler;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
@ConfigurationProperties(prefix = "cassandra")
public class CassandraChronicler implements Chronicler {

    private static Logger logger = LoggerFactory.getLogger(CassandraChronicler.class);

    private CqlSession session;

    private String keyspace = "dhtspace";

    @PostConstruct
    public void postConstruct() {
        logger.info("postConstruct");
        //session = CqlSession.builder().withKeyspace(keyspace).build();
        logger.info("postConstruct done");
    }

    @Override
    public void onPing(@Nonnull Ping command) {
        /*logger.debug("onPing with command = {}", command.toString());
        try {
            PreparedStatement pst = session.prepare("update nodes_stats set pings_requests = pings_requests + 1 where node_id = ?");
            ResultSet res = session.execute(pst.bind("node_id", command.getId()));
            logger.debug("was applied = {}", res.wasApplied());
        } catch (Exception ex) {
            logger.error("!", ex);
        }*/
    }

    @Override
    public void onFindNode(FindNode command) {

    }

    @Override
    public void onGetPeers(GetPeers command) {

    }
}
