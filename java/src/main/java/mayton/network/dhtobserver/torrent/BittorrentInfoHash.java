package mayton.network.dhtobserver.torrent;

import org.apache.commons.lang3.Validate;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class BittorrentInfoHash {

    public final String hash;

    public BittorrentInfoHash(String hash) {
        Validate.isTrue(hash.length() == 32);
        this.hash = hash;
    }
}
