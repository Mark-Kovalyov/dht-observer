package mayton.network.dhtobserver.db.pg;

import com.google.inject.Inject;
import mayton.network.dhtobserver.Chronicler;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

public class PGChronicler implements Chronicler {

    static Marker secured = MarkerManager.getMarker("SECURED");

    static Logger logger = LogManager.getLogger(PGChronicler.class);

    @Inject
    public void init() {
        logger.info(secured, "Connect to PG with user = scott, pwd = tiger");
    }

    @Override
    public void onPing(@NotNull Ping command) {

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


}
