package top.wuhunyu.cap.server.core.utils;

/**
 * 随机工具类
 *
 * @author wuhunyu
 * @date 2025/08/21 16:51
 **/

public final class RandomUtil {

    private RandomUtil() {
    }

    public static int fnv1a(String str) {
        final var n = str.length();
        var hash = (int) 2166136261L;
        for (var i = 0; i < n; i++) {
            hash ^= str.charAt(i);
            hash += (hash << 1) + (hash << 4) + (hash << 7) + (hash << 8) + (hash << 24);
        }
        return hash;
    }

    public static String prng(String seed, int length) {
        var state = RandomUtil.fnv1a(seed);
        final var result = new StringBuilder();
        while (result.length() < length) {
            state ^= state << 13;
            state ^= state >>> 17;
            state ^= state << 5;
            result.append(String.format("%08x", state));
        }
        return result.substring(0, length);
    }

}
