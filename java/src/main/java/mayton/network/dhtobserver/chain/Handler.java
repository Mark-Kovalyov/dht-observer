package mayton.network.dhtobserver.chain;

import mayton.network.dhtobserver.geo.GeoRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Optional;

public interface Handler {

    boolean process(DatagramPacket datagramPacket, Optional<GeoRecord> geoRecordOptional) throws IOException;

    void add(@NotNull Handler next);

    void onHandle();

    void onPass();

    void onError();

    @NotNull String report();
}
