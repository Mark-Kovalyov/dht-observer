package mayton.network.dhtobserver;

import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.geo.GeoDbImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("geo")
@Tag("external-data-source")
@Tag("slow")
class GeoIpDbTest {

    static GeoDb geoDb;

    @BeforeAll
    static void beforeAll() {
        geoDb = new GeoDbImpl();
        ((GeoDbImpl)geoDb).init();
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "207.180.192.205", "217.224.185.25", "183.102.209.178", "136.158.29.2", "217.224.185.25",
            "92.249.115.215", "24.251.249.33"
    })
    void geoDbKnows(String ip) {
        assertTrue(geoDb.findFirst(NetworkUtils.parseIpV4(ip)).isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = { "185.233.194.117" })
    void geoDbDoesntKnow(String ip) {
        assertFalse(geoDb.findFirst(NetworkUtils.parseIpV4(ip)).isPresent());
    }



}
