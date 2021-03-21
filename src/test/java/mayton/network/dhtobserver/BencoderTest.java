package mayton.network.dhtobserver;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import the8472.bencode.BDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

@Tag("bencode")
public class BencoderTest {

    @Test
    @Disabled("Requires real file")
    public void test1() throws IOException {
        BDecoder decoder = new BDecoder();
        InputStream fis = new FileInputStream("out/non-decoded/*");
        fis.skip(20);
        Map<String, Object> res = decoder.decode(ByteBuffer.wrap(IOUtils.toByteArray(fis)));
        String json = Utils.dumpBencodedMapWithJacksonEx(res, new DefaultPrettyPrinter());
        System.out.println(json);
    }

}
