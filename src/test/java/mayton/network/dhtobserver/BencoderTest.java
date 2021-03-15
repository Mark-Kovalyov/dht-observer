package mayton.network.dhtobserver;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import the8472.bencode.BDecoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BencoderTest {

    @Test
    public void test1() throws IOException {
        BDecoder decoder = new BDecoder();
        InputStream fis = new FileInputStream("out/udp-packet-e6ec5bdf-2a21-41eb-af08-d82512abe993");
        Map<String, Object> res = decoder.decode(ByteBuffer.wrap(IOUtils.toByteArray(fis)));
        String json = Utils.dumpBencodedMapWithJacksonEx(res);
        assertEquals("{\"a\":{}}", json);
    }

}
