package mayton.network.dhtobserver;

import javax.annotation.concurrent.Immutable;
import java.net.InetAddress;

@Immutable
public final class UDPConsumerEntity {

    public final byte[] data;
    public final InetAddress inetAddress;
    public final int port;

    public UDPConsumerEntity(byte[] data, InetAddress inetAddress, int port) {
        this.data = data;
        this.inetAddress = inetAddress;
        this.port = port;
    }
}
