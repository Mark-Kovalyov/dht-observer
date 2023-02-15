package mayton.network.dhtobserver;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public interface ConfigProvider {

    @Nonnull
    List<Pair<Integer, String >> threadConfig();

    @Nonnull
    String getNodeId();

}