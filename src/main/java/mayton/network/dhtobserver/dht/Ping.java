package mayton.network.dhtobserver.dht;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Ping implements DhtEvent {

    private final String id;

    private final String lastHostAndPort;

    public Ping(String id, String lastHostAndPort) {
        this.id = id;
        this.lastHostAndPort = lastHostAndPort;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }


    public String getLastHostAndPort() {
        return lastHostAndPort;
    }

    @Override
    public String toString() {
        return "Ping{" +
                "id='" + id + '\'' +
                ", lastIp='" + lastHostAndPort + '\'' +
                '}';
    }
}
