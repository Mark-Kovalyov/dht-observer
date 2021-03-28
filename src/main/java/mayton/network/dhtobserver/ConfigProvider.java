package mayton.network.dhtobserver;

import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.List;

public interface ConfigProvider {

    @Nonnull
    List<Triple<String, Integer, String >> threadConfig();

}
