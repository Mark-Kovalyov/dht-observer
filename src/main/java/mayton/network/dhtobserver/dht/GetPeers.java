package mayton.network.dhtobserver.dht;

import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.net.InetAddress;
import java.util.Optional;

@Immutable
public class GetPeers extends DhtEvent {

    private final String id;

    private final String infoHash;

    public GetPeers(@Nonnull String id, @Nonnull String infoHash, Optional<GeoRecord> optionalGeoRecord, InetAddress inetAddress, int port) {
        super(optionalGeoRecord, inetAddress, port);
        this.id = id;
        this.infoHash = infoHash;
    }

    public String getId() {
        return id;
    }

    public String getInfoHash() {
        return infoHash;
    }
}
