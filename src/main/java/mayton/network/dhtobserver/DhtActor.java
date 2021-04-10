package mayton.network.dhtobserver;

import javax.annotation.Nonnull;

public interface DhtActor {

    void ping(@Nonnull String nodeId);

    void announce(@Nonnull String some);

}
