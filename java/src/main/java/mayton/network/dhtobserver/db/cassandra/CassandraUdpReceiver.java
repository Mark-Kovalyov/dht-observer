package mayton.network.dhtobserver.db.cassandra;

import mayton.network.dhtobserver.db.UDPReceiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class CassandraUdpReceiver implements UDPReceiver {
    @Override
    public void onReceiveUdp(@NotNull String hostIpv4, @Range(from = 0, to = 65535) int port) {

    }
}
