package mayton.network.dhtobserver;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.String.format;

public class Utils {

    static Logger logger = LogManager.getLogger(Utils.class);

    static Random random = new Random();

    public static String wrapValue(Object value) {
        if (value instanceof byte[]) {
            StringJoiner stringJoiner = new StringJoiner(" ", "\"", "\"");
            for (byte b : (byte[]) value) stringJoiner.add(format("%02X", b < 0 ? (int) b + 128 : (int) b));
            return stringJoiner.toString();
        } else {
            return value.toString();
        }
    }

    public static String generateRandomToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) sb.append('a' + random.nextInt('z' - 'a'));
        return sb.toString();
    }

    @NotNull
    public static String dumpDEncodedMap(Map<String, Object> res) {
        StringJoiner stringJoiner = new StringJoiner("," , "{", "}");
        for(Map.Entry<String, Object> item : res.entrySet()) {
            if (item.getValue() instanceof HashMap) {
                stringJoiner.add("\"" + item.getKey() + "\":" + dumpDEncodedMap((Map<String, Object>) item.getValue()));
            } else {
                stringJoiner.add("\"" + item.getKey() + "\":" + wrapValue(item.getValue()));
            }
        }
        return stringJoiner.toString();
    }

    @NotNull
    public static String dumpBencodedMapWithJackson(Map<String, Object> res) {
        return dumpBencodedMapWithJackson(res, new DefaultPrettyPrinter());
    }

    @NotNull
    public static String dumpBencodedMapWithJackson(Map<String, Object> res, PrettyPrinter prettyPrinter) {
        try {
            return dumpBencodedMapWithJacksonEx(res, prettyPrinter);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
        return "";
    }

    public static void dumpBencodedMapWithJacksonEx(String rootName, Map<String, Object> res, JsonGenerator jGenerator) throws IOException {
        if (rootName == null) {
            jGenerator.writeStartObject();
        } else {
            jGenerator.writeObjectFieldStart(rootName);
        }

        for (Map.Entry<String, Object> item : res.entrySet()) {
            Object value = item.getValue();
            if (value instanceof String) {
                jGenerator.writeStringField(item.getKey(), (String) value);
            } else if (value instanceof Integer) {
                jGenerator.writeNumberField(item.getKey(), (Integer) item.getValue());
            } else if (value instanceof Long) {
                jGenerator.writeNumberField(item.getKey(), (Long) item.getValue());
            } else if (value instanceof List) {
                jGenerator.writeArrayFieldStart(item.getKey());
                for (Object o : (List) value) {
                    if (o instanceof Integer) {
                        jGenerator.writeNumber((Integer) o);
                    } else if (o instanceof String) {
                        jGenerator.writeString((String) o);
                    } else {
                        logger.error("Unable to detect value class = {} during array generation", o.getClass().toString());
                    }
                }
                jGenerator.writeEndArray();
            } else if (value instanceof Map) {
                dumpBencodedMapWithJacksonEx(item.getKey(), (Map) value, jGenerator);
            } else if (value instanceof byte[]) {
                // Sometimes it's possible that byte[] is a String, for example
                // "q" : "ping"
                jGenerator.writeStringField(item.getKey(), beautifyBlob((byte[]) value));
            } else {
                logger.error("Unable to detect value class = {} during map generation", value.getClass());
            }
        }
        jGenerator.writeEndObject();
    }

    private static String beautifyBlob(byte[] arr) {
        if (allContainsAscii(arr)) {
            return Hex.encodeHexString(arr) + " ( '" + new String(arr) + "' )";
        } else {
            return Hex.encodeHexString(arr);
        }
    }

    private static boolean allContainsAscii(byte[] arr) {
        for(int i = 0 ;i<arr.length;i++) {
            if (arr[i] > 32) continue;
            else return false;
        }
        return true;
    }

    public static String dumpBencodedMapWithJacksonEx(Map<String, Object> res, PrettyPrinter prettyPrinter) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
        jGenerator.setPrettyPrinter(prettyPrinter);
        dumpBencodedMapWithJacksonEx(null, res, jGenerator);
        jGenerator.flush();
        stream.flush();
        return stream.toString(StandardCharsets.UTF_8);
    }



}
