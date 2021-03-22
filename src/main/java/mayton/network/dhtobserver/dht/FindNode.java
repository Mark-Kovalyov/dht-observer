package mayton.network.dhtobserver.dht;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FindNode implements DhtEvent {

    private final String id;
    private final String target;

    public FindNode(String id, String target) {
        this.id = id;
        this.target = target;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }
}
