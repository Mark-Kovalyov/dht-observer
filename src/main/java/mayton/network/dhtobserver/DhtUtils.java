package mayton.network.dhtobserver;

import javax.annotation.Nonnull;
import java.math.BigInteger;

/**
 * In Kademlia, the distance metric is XOR and the result is interpreted as an unsigned integer.
 * distance(A,B) = |A xor B| Smaller values are closer.
 */
public class DhtUtils {

    @Nonnull
    public static byte[] xorDistance(byte[] a, byte[] b) {
        return new byte[0];
    }

    @Nonnull
    public static BigInteger xorDistance(String a, String b) {
        return BigInteger.ONE;
    }

}
