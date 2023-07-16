package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import com.google.inject.Inject;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

import static mayton.network.NetworkUtils.formatIpV4;
import static mayton.network.NetworkUtils.fromIpv4toLong;


@Deprecated
public class CassandraChronicler extends CassandraConnectionComponent implements Chronicler {

    private static Logger logger = LoggerFactory.getLogger(CassandraChronicler.class);

    private static final int NODE_HOST_TTL = 7 * 24 * 60 * 60;   // 7 days to keep host info
    private static final int INFO_HASH_TTL = 1 * 60 * 60;        // 1 hour to keep tokens
    private static final int ANNOUNCE_TTL  = 30 * 24 * 60 * 60;  // 30 days

    @Inject
    public void init() {
        session = CqlSession.builder().withKeyspace(keyspace).build();
    }

    @Override
    public void onPing(@Nonnull Ping command) {
        logger.debug("onPing with command = {}", command);
        try {
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
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
                        command.getGeoRecord().orElseThrow().country,
                        command.getGeoRecord().orElseThrow().city,
                        command.getId());
            } else {
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                "WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getId());
            }

        } catch (Exception ex) {
            logger.error("onPing error", ex);
        }
    }

    @Override
    public void onFindNode(FindNode command) {
        logger.debug("onFindNode with command = {}", command);
        try {
            // CREATE TABLE dhtspace.known_peers (
            //    seq int PRIMARY KEY,
            //    host text,
            //    last_update_time timestamp
            //)
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
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
                        command.getGeoRecord().orElseThrow().country,
                        command.getGeoRecord().orElseThrow().city,
                        command.getId());
            } else {
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getId());
            }
        } catch (Exception ex) {
            // cqlsh:dhtspace> desc nodes_hosts;
            //
            //CREATE TABLE dhtspace.nodes_hosts (
            //    node_id text PRIMARY KEY,
            //    last_city text,
            //    last_country text,
            //    last_ip_port text,
            //    last_update_time timestamp
            //)
            // com.datastax.oss.driver.api.core.servererrors.InvalidQueryException: Non PRIMARY KEY columns found in where clause: host
            logger.error("onFindNode error", ex);
        }
    }

    @Override
    public void onGetPeers(GetPeers command) {
        logger.debug("onGetPeers with command = {}", command);
        try {
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
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
                        command.getGeoRecord().orElseThrow().country,
                        command.getGeoRecord().orElseThrow().city,
                        command.getId());
            } else {
                sessionAction(
                        "UPDATE nodes_hosts USING TTL " + NODE_HOST_TTL + " SET " +
                                " last_ip_port = ?, " +
                                " last_update_time = toTimeStamp(now()) " +
                                " WHERE node_id = ?",
                        command.getHostAndPort(),
                        command.getId());
            }
        } catch (Exception ex) {
            logger.error("onGetPeers error", ex);
        }
    }

    @Override
    public void onAnnouncePeer(@NotNull AnnouncePeer command) {
        logger.debug("onAnnouncePeer with command = {}", command);
        try {
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
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
            logger.error("onAnnouncePeer error", ex);
        }
    }

    @Override
    public void close() {
        session.close();
    }

}
