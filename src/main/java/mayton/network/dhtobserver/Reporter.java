package mayton.network.dhtobserver;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Reporter extends AutoCloseable {

    @NotNull
    List<String> knownPeers();

}
