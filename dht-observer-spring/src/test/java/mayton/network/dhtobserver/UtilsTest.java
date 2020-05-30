package mayton.network.dhtobserver;

import mayton.network.NetworkUtils;
import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    public void test() {
        long minIpV4 = NetworkUtils.parseIpV4("0.0.0.0");
        long maxIpV4 = NetworkUtils.parseIpV4("255.255.255.255"); // 4_294_967_295
        int x =0;
    }

}
