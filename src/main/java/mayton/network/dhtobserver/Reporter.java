package mayton.network.dhtobserver;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Reporter {

    @NotNull
    List<String> knownPeers();

    void close();

}
