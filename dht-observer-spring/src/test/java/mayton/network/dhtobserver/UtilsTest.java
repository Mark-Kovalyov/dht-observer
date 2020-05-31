package mayton.network.dhtobserver;

import mayton.network.NetworkUtils;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {

    @Test
    public void testDumpDEncodedMapJackson() {
        assertEquals("{}", Utils.dumpDEncodedMapJackson(Collections.EMPTY_MAP));

        assertEquals("{}", Utils.dumpDEncodedMapJackson(new LinkedHashMap() {{
            put("key1", "value1");
            put("key2", Integer.valueOf(1));
        }}));
    }

}
