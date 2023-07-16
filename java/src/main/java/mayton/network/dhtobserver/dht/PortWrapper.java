package mayton.network.dhtobserver.dht;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Range;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PortWrapper {

    public final int port;

    public PortWrapper(@Range(from = 0, to = 65535) int port) {
        this.port = port;
    }
}
