package mayton.network.dhtobserver.torrent;

import io.vavr.control.Either;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class TorrentTrackerClient implements TorrentTracker {
    @Override
    public Either<TorrentTrackerResponce, Integer> getById(BittorrentInfoHash btih) {

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(15))
                .setConnectionRequestTimeout(Timeout.ofSeconds(15))
                .build();

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();

        try {
            // compact=0 & event=started & peer_id=12345678987654321234 & port=6881 &
            // info_hash= &
            // left=200075
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https");
            builder.setHost("my-track.info");
            builder.setPath("/announce");
            builder.addParameter("uploaded", "0");
            builder.addParameter("downloaded", "0");
            builder.addParameter("info_hash", "%18%28n%23K%ECt%B7%93S%C5%F1-%F3%1C%18k%CEX%A4");

            URL url = builder.build().toURL();

            HttpPut httpPut;
            MultipartEntityBuilder multipartEntityBuilder;

            HttpResponse response = client.execute(new HttpGet(url.toURI()));

            if (response.getCode() == HttpStatus.SC_OK) {
                return Either.left(new TorrentTrackerResponce());
            } else {
                return Either.right(response.getCode());
            }
        } catch (IOException e) {
            return Either.right(0);
        } catch (URISyntaxException e) {
            return Either.right(0);
        }

    }
}
