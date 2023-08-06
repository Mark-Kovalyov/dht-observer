package mayton.network.dhtobserver;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DhtCommandsTest {

    @Test
    void test_ping() {
        assertEquals("70696e67", Hex.encodeHexString("ping".getBytes()));
    }

    @Test
    void test_find_node() {
        assertEquals("66696e645f6e6f6465", Hex.encodeHexString("find_node".getBytes()));
    }

    @Test
    void test_announce_peer() {
        assertEquals("616e6e6f756e63655f70656572", Hex.encodeHexString("announce_peer".getBytes()));
    }

    @Test
    void test_get_peers() {
        assertEquals("616e6e6f756e63655f70656572", Hex.encodeHexString("get_peers".getBytes()));
    }

}
