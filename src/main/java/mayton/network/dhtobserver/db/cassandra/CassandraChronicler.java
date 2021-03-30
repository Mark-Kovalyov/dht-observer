package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import com.google.inject.Inject;
import mayton.network.dhtobserver.Chronicler;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public class CassandraChronicler implements Chronicler {

    private static Logger logger = LoggerFactory.getLogger(CassandraChronicler.class);

    private CqlSession session;

    private String keyspace = "dhtspace";

    private int NODE_HOST_TTL = 7 * 24 * 60 * 60;   // 7 days to keep host info
    private int INFO_HASH_TTL = 1 * 60 * 60;        // 1 hour to keep tokens
    private int ANNOUNCE_TTL  = 30 * 24 * 60 * 60;  // 30 days

    @Inject
    public void init() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
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
            sessionAction("UPDATE nodes_stats SET pings_requests = pings_requests + 1 WHERE node_id = ?", command.getId());
            if (command.getGeoRecord().isPresent()) {
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_country = ?, " +
                                " last_city = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getGeoRecord().get().country,
                        command.getGeoRecord().get().city,
                        command.getId());
            } else {
                logger.warn("Hmm... unable to recognize GeoIp binding for ip = {}", command.getHostAndPort());
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                "WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getId());
            }

        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void onFindNode(FindNode command) {
        logger.debug("onFindNode with command = {}", command.toString());
        try {
            sessionAction("UPDATE nodes_stats SET find_nodes_requests = find_nodes_requests + 1 WHERE node_id = ?", command.getId());
            sessionAction("UPDATE targets SET x = 1 WHERE target_id = ? and node_id = ?", command.getTarget(), command.getId());
            if (command.getGeoRecord().isPresent()) {
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()), " +
                                " last_country = ?, " +
                                " last_city    = ? " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getGeoRecord().get().country,
                        command.getGeoRecord().get().city,
                        command.getId());
            } else {
                logger.warn("Hmm... unable to recognize GeoIp binding for ip = {}", command.getHostAndPort());
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getId());
            }
        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void onGetPeers(GetPeers command) {
        logger.debug("onGetPeers with command = {}", command.toString());
        try {
            sessionAction("UPDATE info_hash USING TTL " + INFO_HASH_TTL + " SET info_hash = ? WHERE node_id = ?", command.getInfoHash(), command.getId());
            sessionAction("UPDATE nodes_stats SET get_peeers_requests = get_peeers_requests + 1 WHERE node_id = ?", command.getId());
            if (command.getGeoRecord().isPresent()) {
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()), " +
                                " last_country = ?, " +
                                " last_city    = ? " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getGeoRecord().get().country,
                        command.getGeoRecord().get().city,
                        command.getId());
            } else {
                logger.warn("Hmm... unable to recognize GeoIp binding for ip = {}", command.getHostAndPort());
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getId());
            }
        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void onAnnouncePeer(@NotNull AnnouncePeer command) {
        logger.debug("onAnnouncePeer with command = {}", command.toString());
        try {
            sessionAction("UPDATE announces USING TTL " + ANNOUNCE_TTL + " SET" +
                            " node_id = ?, " +
                            " token_value = ?, " +
                            " last_update_time = toTimeStamp(now())" +
                            " WHERE info_hash = ?",
                    command.getId(),
                    command.getToken(),
                    command.getInfoHash());

            sessionAction("UPDATE nodes_stats SET announce_requests = announce_requests + 1 WHERE node_id = ?", command.getId());

        } catch (Exception ex) {
            logger.error("!", ex);
        }
    }

    @Override
    public void close() {
        session.close();
    }

}
