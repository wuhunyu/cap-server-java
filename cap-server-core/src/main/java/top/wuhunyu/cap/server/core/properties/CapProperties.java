package top.wuhunyu.cap.server.core.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * cap 属性配置
 *
 * @author wuhunyu
 * @date 2025/06/16 11:16
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CapProperties {

    /**
     * 挑战数量
     */
    private Integer challengeCount;

    /**
     * 挑战大小
     */
    private Integer challengeSize;

    /**
     * 挑战难度
     */
    private Integer challengeDifficulty;

    /**
     * 挑战过期时间，单位：毫秒
     */
    private Long challengeExpiresMs;

    /**
     * token 过期时间，单位：毫秒
     */
    private Long tokenExpiresMs;

    /**
     * id 大小
     */
    private Long idSize;

    /**
     * token 分隔符，一般为:，不需要修改
     */
    private String tokenKeySplitter;

}
