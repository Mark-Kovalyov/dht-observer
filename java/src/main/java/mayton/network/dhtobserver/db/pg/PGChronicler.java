package mayton.network.dhtobserver.db.pg;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class PGChronicler implements Chronicler {

    static Marker secured = MarkerManager.getMarker("SECURED");

    static Logger logger = LogManager.getLogger(PGChronicler.class);

    private HikariConfig hikariConfig = new HikariConfig();

    private HikariDataSource ds;

    private String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/torrentdb";
    private String username = "mayton";
    private String password = "";
    private String driverClassName = "";

    @Inject
    public void init() {
        logger.info(secured, "Connect to PG with user = scott, pwd = tiger");
        logger.info(":: init for JDBCConnectionPoolComponentImpl");

        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("minimumIdle", 5);
        hikariConfig.addDataSourceProperty("maximumPoolSize", 20);
        hikariConfig.addDataSourceProperty("idleTimeout", 30000);
        hikariConfig.addDataSourceProperty("maxLifetime", 2000000);
        hikariConfig.addDataSourceProperty("connectionTimeout", 30000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 4);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 4);

        ds = new HikariDataSource(hikariConfig);
    }

    @Override
    public void onPing(@NotNull Ping command) {
        try {
            Connection conn = ds.getConnection();
            conn.prepareStatement(
                    "INSERT INTO nodes(id) values(?)" +
            "");
            conn.close();
        } catch (SQLException e) {
            logger.warn("Ex:", e);
        }
    }

    @Override
    public void onFindNode(@NotNull FindNode command) {

    }

    @Override
    public void onGetPeers(@NotNull GetPeers command) {

    }

    @Override
    public void onAnnouncePeer(@NotNull AnnouncePeer command) {

    }

    @Override
    public void close() {

    }


}
