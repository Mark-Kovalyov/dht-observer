package mayton.network.dhtobserver;

import mayton.network.dhtobserver.dht.*;

import javax.annotation.Nonnull;

public interface Chronicler extends AutoCloseable {

    default void onEvent(@Nonnull DhtEvent command) {
        if (command instanceof Ping) {
            onPing((Ping) command);
        } else if (command instanceof GetPeers) {
            onGetPeers((GetPeers) command);
        } else if (command instanceof FindNode) {
            onFindNode((FindNode) command);
        } else if (command instanceof AnnouncePeer) {
            onAnnouncePeer((AnnouncePeer) command);
        }
    }

    void onPing(@Nonnull Ping command);

    void onFindNode(@Nonnull FindNode command);

    void onGetPeers(@Nonnull GetPeers command);

    void onAnnouncePeer(@Nonnull AnnouncePeer command);

}
