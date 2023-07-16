package mayton.network.dhtobserver.torrent;

import io.vavr.control.Either;

public interface TorrentTracker {

    Either<TorrentTrackerResponce,Integer> getById(BittorrentInfoHash btih);

}
