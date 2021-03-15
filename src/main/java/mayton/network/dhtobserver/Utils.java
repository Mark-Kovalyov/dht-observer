package mayton.network.dhtobserver;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import static java.lang.String.format;

public class Utils {

    static Logger logger = LogManager.getLogger(Utils.class);

    public static String wrapValue(Object value) {
        if (value instanceof byte[]) {
            StringJoiner stringJoiner = new StringJoiner(" ", "\"", "\"");
            for (byte b : (byte[]) value) stringJoiner.add(format("%02X", b < 0 ? (int) b + 128 : (int) b));
            return stringJoiner.toString();
        } else {
            return value.toString();
        }
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
        try {
            return dumpBencodedMapWithJacksonEx(res);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
        return "";
    }

    public static void dumpBencodedMapWithJacksonEx(String rootName, Map<String, Object> res, JsonGenerator jGenerator) throws IOException {
        if (rootName == null)
             jGenerator.writeStartObject();
        else
             jGenerator.writeObjectFieldStart(rootName);

        for (Map.Entry<String, Object> item : res.entrySet()) {
            Object value = item.getValue();
            if (value instanceof String) {
                jGenerator.writeStringField(item.getKey(), (String) value);
            } else if (value instanceof Integer) {
                jGenerator.writeNumberField(item.getKey(), (Integer) item.getValue());
            } else if (value instanceof List) {
                jGenerator.writeArrayFieldStart(item.getKey());
                for (Object o : (List) value) {
                    if (o instanceof Integer) {
                        jGenerator.writeNumber((Integer) o);
                    } else if (o instanceof String) {
                        jGenerator.writeString((String) o);
                    } else {
                        logger.warn("Unable to detect value class = {}", o.getClass().toString());
                    }
                }
                jGenerator.writeEndArray();
            } else if (value instanceof Map) {
                dumpBencodedMapWithJacksonEx(item.getKey(), (Map) value, jGenerator);
            } else if (value instanceof byte[]) {
                // Sometimes it's possible that byte[] is a String, for example
                // "q" : "ping"
                jGenerator.writeStringField(item.getKey(), Hex.encodeHexString((byte[]) value));
            } else {
                logger.warn("Unable to detect value class = {}", value.getClass());
            }
        }
        jGenerator.writeEndObject();
    }

    public static String dumpBencodedMapWithJacksonEx(Map<String, Object> res) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator;
        jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
        jGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
        dumpBencodedMapWithJacksonEx(null, res, jGenerator);
        jGenerator.flush();
        stream.flush();
        return new String(stream.toByteArray());
    }

    @NotNull
    public static String binhex(@NotNull byte[] data) {
        return binhex(data, false);
    }

    @NotNull
    public static String binhex(@NotNull byte[] data, boolean truncateZeroes) {
        String splitter = " : ";
        StringBuilder sb = new StringBuilder(data.length * 2 + 1);
        sb.append("\n");
        int cnt = 0;
        int k = data.length - 1;
        if (truncateZeroes) {
            while(data[k] == 0 && k > 0) k--;
        }
        StringBuilder chars = new StringBuilder();
        StringBuilder bytes = new StringBuilder();
        int offset = 0;
        for (int i = 0; i < k; i++) {
            bytes.append(format("%02X ",data[i]));
            chars.append(data[i] >= 32 ? (char) data[i] : ' ');
            cnt++;
            if (cnt > 16) {
                cnt = 0;
                sb.append(format("%08X : ", offset))
                        .append(bytes)
                        .append(splitter)
                        .append(chars)
                        .append("\n");
                chars = new StringBuilder();
                bytes = new StringBuilder();
                offset += 16;
            }
        }
        if (cnt > 0) {
            sb.append(format("%08X : ", offset))
                    .append(bytes)
                    .append(String.join("", Collections.nCopies(17 - cnt, "   ")))
                    .append(splitter)
                    .append(chars)
                    .append("\n");
        }
        return sb.toString();
    }


}
