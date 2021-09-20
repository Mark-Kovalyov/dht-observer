package mayton.network.dhtobserver.db;

import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;

public interface UDPReceiver {

    void onReceiveUdp(@Nonnull String hostIpv4, @Range(from = 0, to = 65535) int port);

}
