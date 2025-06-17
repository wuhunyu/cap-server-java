package top.wuhunyu.cap.server.core.constants;

import top.wuhunyu.cap.server.core.properties.StoreType;

/**
 * cap server 常量
 *
 * @author wuhunyu
 * @date 2025/06/16 15:32
 **/

public final class CapConstants {

    private CapConstants() {
    }

    public static final StoreType DEFAULT_STORE_TYPE = StoreType.IN_MEMORY;

    public static final int DEFAULT_CHALLENGE_COUNT = 50;

    public static final int DEFAULT_CHALLENGE_SIZE = 32;

    public static final int DEFAULT_CHALLENGE_DIFFICULTY = 4;

    public static final long DEFAULT_CHALLENGE_EXPIRES_MS = 600000L;

    public static final long DEFAULT_TOKEN_EXPIRES_MS = 1200000L;

    public static final long DEFAULT_ID_SIZE = 16L;

    public static final String DEFAULT_TOKEN_KEY_SPLITTER = ":";

}
