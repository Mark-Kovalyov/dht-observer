package mayton.network.dhtobserver.dht;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class AnnouncePeer implements DhtEvent{

    private final String id;

    public AnnouncePeer(String id) {
        this.id = id;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }
}
