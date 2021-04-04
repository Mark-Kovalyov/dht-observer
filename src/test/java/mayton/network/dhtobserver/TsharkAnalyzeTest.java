package mayton.network.dhtobserver;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import the8472.bencode.BDecoder;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TsharkAnalyzeTest {

    @Test
    @Disabled
    public void test() throws Exception {
        String[] hexs = {
                ""
        };

        for(String hex : hexs) {
            BDecoder decoder = new BDecoder();
            Map<String, Object> res = decoder.decode(ByteBuffer.wrap(Hex.decodeHex(hex)));
            System.out.println(Utils.dumpBencodedMapWithJackson(res));
        }
    }

    @Test
    public void test2() {
        int SHA1_BITS = 160;
        int SHA1_BYTES = SHA1_BITS / 8;
        int SHA1_HEX_CHARS = SHA1_BYTES * 2;

        String nodes = "e2659c2edb952aa5f1a037d8140d1370f77d8319";

        assertEquals(1, nodes.length() / SHA1_HEX_CHARS);
        assertEquals(0, nodes.length() % SHA1_HEX_CHARS);
    }

}
