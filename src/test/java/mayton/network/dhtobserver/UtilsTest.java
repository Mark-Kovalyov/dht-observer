package mayton.network.dhtobserver;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Disabled
public class UtilsTest {

    @Test
    public void testDumpDEncodedMapJackson1() {
        assertEquals("{}", Utils.dumpBencodedMapWithJackson(Collections.EMPTY_MAP));
    }

    @Test
    public void testDumpDEncodedMapJackson2() {
        assertEquals("{\"key1\":\"value1\",\"key2\":1}", Utils.dumpBencodedMapWithJackson(new LinkedHashMap() {{
            put("key1", "value1");
            put("key2", Integer.valueOf(1));
        }}));
    }

    @Test
    public void testDumpDEncodedMapJackson3() {
        assertEquals("{\"key1\":[]}", Utils.dumpBencodedMapWithJackson(new LinkedHashMap() {{
            put("key1", Collections.EMPTY_LIST);
        }}));
    }

    @Test
    public void testDumpDEncodedMapJackson4() {
        assertEquals("{\"key1\":[1,2]}", Utils.dumpBencodedMapWithJackson(new LinkedHashMap() {{
            put("key1", Arrays.asList(1, 2));
        }}));
    }

    @Test
    public void testDumpDEncodedMapJackson5() {
        assertEquals("{\"key1\":[\"1\",\"2\"]}", Utils.dumpBencodedMapWithJackson(new LinkedHashMap() {{
            put("key1", Arrays.asList("1", "2"));
        }}));
    }

    @Test
    public void testDumpDEncodedMapJackson6() {
        assertEquals("{\"key1\":{\"sub-key1\":\"sub-value1\"}}", Utils.dumpBencodedMapWithJackson(new LinkedHashMap() {{
            put("key1", new LinkedHashMap() {{
                put("sub-key1", "sub-value1");
            }});
        }}));
    }



}
