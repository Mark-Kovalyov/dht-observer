package mayton.network.dhtobserver;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

class TsharkAnalyzeTest {



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
