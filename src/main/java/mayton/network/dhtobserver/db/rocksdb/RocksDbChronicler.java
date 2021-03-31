package mayton.network.dhtobserver.db.rocksdb;

import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.dht.AnnouncePeer;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;
import org.jetbrains.annotations.NotNull;

public class RocksDbChronicler implements Chronicler {

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
