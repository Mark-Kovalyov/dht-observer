package mayton.network.dhtobserver.db;

import java.util.List;

public interface Reporter extends AutoCloseable {

    @NotNull
    List<String> knownPeers();

}
