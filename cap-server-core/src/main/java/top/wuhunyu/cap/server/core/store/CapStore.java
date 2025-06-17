package top.wuhunyu.cap.server.core.store;

import top.wuhunyu.cap.server.core.model.Challenge;
import top.wuhunyu.cap.server.core.properties.StoreType;

import java.time.Instant;

/**
 * cap 存储
 *
 * @author wuhunyu
 * @date 2025/06/16 16:13
 **/

public interface CapStore {

    /**
     * 存储类型
     *
     * @return 存储类型
     */
    StoreType storeType();

    /**
     * 存储 挑战
     *
     * @param token     token
     * @param challenge 挑战
     * @return 是否存储成功(true : 成功 ; false : 失败)
     */
    boolean putChallenge(String token, Challenge challenge);

    /**
     * 删除 挑战
     *
     * @param token token
     * @return 返回被删除的挑战，如果token不存在则为空
     */
    Challenge removeChallenge(String token);

    /**
     * 获取指定 token 的挑战
     *
     * @param token token
     * @return 挑战，可能为空
     */
    Challenge getChallenge(String token);

    /**
     * 存储 token
     *
     * @param token   token
     * @param expires 过期时间
     * @return 是否存储成功(true : 成功 ; false : 失败)
     */
    boolean putToken(String token, Instant expires);

    /**
     * 删除 token
     *
     * @param token token
     * @return 返回被删除 token 的过期时间，如果 token 不存在则为空
     */
    Instant removeToken(String token);

    /**
     * 获取 token 的过期时间
     *
     * @param token token
     * @return 过期时间，如果 token 不存在则为空
     */
    Instant getToken(String token);

    /**
     * <p>清理过期的 挑战</p>
     * <p>清理策略是后台定时清理</p>
     */
    void clean();

}
