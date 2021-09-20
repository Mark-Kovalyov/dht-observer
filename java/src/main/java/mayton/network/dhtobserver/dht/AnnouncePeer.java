package mayton.network.dhtobserver.dht;

import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.net.InetAddress;
import java.util.Optional;

//  {
//  "a" : {
//    "id" : "842ca6cee2344eb7af82a21687d918615f9451e6",
//    "info_hash" : "c3c66bcd081558d1297fd65e1803988f54b82935",
//    "token" : "dbd47bcfa0715529"
//    "port" : 8080
//  },
//  "q" : "616e6e6f756e63655f70656572 ( 'announce_peer' )",
//  "t" : "1b6e0900",
//  "y" : "71 ( 'q' )"
//}
@Immutable
public final class AnnouncePeer extends DhtEvent{

    private final String id;

    private final String infoHash;

    private final String token;

    private final int port;

    public AnnouncePeer(String id, String infoHash, String token, int port, Optional<GeoRecord> geoRecord, InetAddress inetAddress) {
        super(geoRecord, inetAddress, port);
        this.id = id;
        this.infoHash = infoHash;
        this.token = token;
        this.port = port;
    }


    public String getId() {
        return id;
    }

    public String getInfoHash() {
        return infoHash;
    }

    public String getToken() {
        return token;
    }

    public int getPort() {
        return port;
    }
}
