package mayton.network.dhtobserver;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static java.lang.String.format;

public class Utils {

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
