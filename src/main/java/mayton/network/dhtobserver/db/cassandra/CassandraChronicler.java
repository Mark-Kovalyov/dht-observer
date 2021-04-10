package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.UDPReceiver;
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
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
            sessionAction("UPDATE port_stats SET hits = hits + 1 WHERE port = ?", command.getPort());
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
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
            sessionAction("UPDATE port_stats SET hits = hits + 1 WHERE port = ?", command.getPort());
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
        // [ERROR] 22  : mayton.network.dhtobserver.db.cassandra.CassandraChronicler !
        // com.datastax.oss.driver.api.core.servererrors.InvalidQueryException: PRIMARY KEY part last_update_time found in SET part
        //	at com.datastax.oss.driver.api.core.servererrors.InvalidQueryException.copy(InvalidQueryException.java:48) ~[java-driver-core-4.10.0.jar:?]
        //	at com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures.getUninterruptibly(CompletableFutures.java:149) ~[java-driver-core-4.10.0.jar:?]
        //	at com.datastax.oss.driver.internal.core.cql.CqlPrepareSyncProcessor.process(CqlPrepareSyncProcessor.java:59) ~[java-driver-core-4.10.0.jar:?]
        //	at com.datastax.oss.driver.internal.core.cql.CqlPrepareSyncProcessor.process(CqlPrepareSyncProcessor.java:31) ~[java-driver-core-4.10.0.jar:?]
        //	at com.datastax.oss.driver.internal.core.session.DefaultSession.execute(DefaultSession.java:230) ~[java-driver-core-4.10.0.jar:?]
        //	at com.datastax.oss.driver.api.core.cql.SyncCqlSession.prepare(SyncCqlSession.java:224) ~[java-driver-core-4.10.0.jar:?]
        //	at mayton.network.dhtobserver.db.cassandra.CassandraChronicler.sessionAction(CassandraChronicler.java:41) ~[dht-observer.jar:?]
        //	at mayton.network.dhtobserver.db.cassandra.CassandraChronicler.onGetPeers(CassandraChronicler.java:121) [dht-observer.jar:?]
        //	at mayton.network.dhtobserver.UDPConsumer.decodeCommand(UDPConsumer.java:168) [dht-observer.jar:?]
        //	at mayton.network.dhtobserver.UDPConsumer.run(UDPConsumer.java:101) [dht-observer.jar:?]
        //	at java.lang.Thread.run(Thread.java:834) [?:?]
        logger.debug("onGetPeers with command = {}", command.toString());
        try {
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
            sessionAction("UPDATE port_stats SET hits = hits + 1 WHERE port = ?", command.getPort());
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
            //sessionAction("UPDATE known_peers SET last_update_time = toTimeStamp(now()) WHERE host = ? AND seq = 1", formatIpV4(fromIpv4toLong(command.getInetAddress())));
            sessionAction("UPDATE port_stats SET hits = hits + 1 WHERE port = ?", command.getPort());
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
