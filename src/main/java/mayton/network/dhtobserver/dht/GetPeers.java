package mayton.network.dhtobserver.dht;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public class GetPeers implements DhtEvent {

    private final String id;

    public GetPeers(String id) {
        this.id = id;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }
}
