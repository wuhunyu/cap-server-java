package top.wuhunyu.cap.server.autoconfigure;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import top.wuhunyu.cap.server.core.properties.CapProperties;
import top.wuhunyu.cap.server.core.store.CapStore;
import top.wuhunyu.cap.server.extra.store.RedisStore;

/**
 * cap server redis 存储自动装配
 *
 * @author wuhunyu
 * @date 2025/06/17 09:39
 **/

@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class CapServerRedisStoreAutoConfigure {

    @Bean("capStore")
    @ConditionalOnMissingBean(name = "capStore")
    public CapStore capStore(
            RedissonClient redissonClient,
            CapProperties capProperties
    ) {
        return new RedisStore(
                redissonClient,
                capProperties
        );
    }

}
