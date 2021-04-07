package mayton.network.dhtobserver;

import org.jetbrains.annotations.NotNull;

public interface DhtActor {

    void ping(@NotNull String nodeId);

    void announce(@NotNull String some);

}
