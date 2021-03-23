package mayton.network.dhtobserver.dht;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FindNode implements DhtEvent {

    private final String id;
    private final String target;
    private final String hostAndPort;

    public FindNode(String id, String target, String hostAndPort) {
        this.id = id;
        this.target = target;
        this.hostAndPort = hostAndPort;
    }


    @NotNull
    @Override
    public String getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "FindNode{" +
                "id='" + id + '\'' +
                ", target='" + target + '\'' +
                ", hostAndPort='" + hostAndPort + '\'' +
                '}';
    }

    public String getHostAndPort() {
        return hostAndPort;
    }
}
