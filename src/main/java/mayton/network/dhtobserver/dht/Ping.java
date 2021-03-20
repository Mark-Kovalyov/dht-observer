package mayton.network.dhtobserver.dht;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Ping implements DhtEvent {

    private final String id;

    public Ping(String id) {
        this.id = id;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Ping{" +
                "id='" + id + '\'' +
                '}';
    }
}
