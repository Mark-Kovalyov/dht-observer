package mayton.network.dhtobserver.db.ignite;

import com.google.inject.Inject;
import mayton.network.dhtobserver.DhtObserverApplication;
import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IgniteChronicler implements Chronicler {

    static Logger logger = LogManager.getLogger(IgniteChronicler.class);

    @Inject
    public void init() {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("127.0.0.1:10800");
        try (IgniteClient igniteClient = Ignition.startClient(cfg)) {

        } catch (Exception e) {
            logger.error(e);
        }
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

    @Override
    public void close() throws Exception {

    }
}
