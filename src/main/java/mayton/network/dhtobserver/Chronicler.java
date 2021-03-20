package mayton.network.dhtobserver;

import mayton.network.dhtobserver.dht.DhtEvent;
import mayton.network.dhtobserver.dht.FindNode;
import mayton.network.dhtobserver.dht.GetPeers;
import mayton.network.dhtobserver.dht.Ping;

import javax.annotation.Nonnull;

public interface Chronicler {

    default void onEvent(@Nonnull DhtEvent command) {
        if (command instanceof Ping) {
            onPing((Ping) command);
        } else if (command instanceof GetPeers) {
            onGetPeers((GetPeers) command);
        } else if (command instanceof FindNode) {
            onFindNode((FindNode) command);
        }
    }

    void onPing(@Nonnull Ping command);

    void onFindNode(@Nonnull FindNode command);

    void onGetPeers(@Nonnull GetPeers command);

}
