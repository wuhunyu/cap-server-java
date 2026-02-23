package top.wuhunyu.cap.server.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import top.wuhunyu.cap.server.core.CapServer;
import top.wuhunyu.cap.server.core.handler.CapHandler;
import top.wuhunyu.cap.server.core.handler.DateHandler;
import top.wuhunyu.cap.server.core.properties.CapProperties;
import top.wuhunyu.cap.server.core.store.CapStore;
import top.wuhunyu.cap.server.core.store.InMemoryStore;

import java.util.Map;
import java.util.Optional;

import static top.wuhunyu.cap.server.core.constants.CapConstants.*;

/**
 * cap server 自动装配
 *
 * @author wuhunyu
 * @date 2025/06/16 18:48
 **/

@Slf4j
public class CapServerAutoConfigure {

    @Bean("dateHandler")
    @ConditionalOnMissingBean(DateHandler.class)
    public DateHandler dateHandler() {
        return new DateHandler();
    }

    @Bean("capProperties")
    @ConditionalOnMissingBean(CapProperties.class)
    public CapProperties capProperties(Environment environment) {
        // 绑定属性
        final var capProperties = Binder.get(environment)
                .bind("cap.server", CapProperties.class)
                .orElseGet(() -> {
                    return CapProperties.builder()
                            .challengeCount(DEFAULT_CHALLENGE_COUNT)
                            .challengeSize(DEFAULT_CHALLENGE_SIZE)
                            .challengeDifficulty(DEFAULT_CHALLENGE_DIFFICULTY)
                            .challengeExpiresMs(DEFAULT_CHALLENGE_EXPIRES_MS)
                            .tokenExpiresMs(DEFAULT_TOKEN_EXPIRES_MS)
                            .idSize(DEFAULT_ID_SIZE)
                            .tokenKeySplitter(DEFAULT_TOKEN_KEY_SPLITTER)
                            .build();
                });
        // 填充默认值
        this.fillDefault(capProperties);
        // 打印属性配置
        this.logCapProperties(capProperties);
        return capProperties;
    }

    private void fillDefault(CapProperties capProperties) {
        capProperties.setChallengeCount(
                Optional.ofNullable(capProperties.getChallengeCount())
                        .orElse(DEFAULT_CHALLENGE_COUNT)
        );
        capProperties.setChallengeSize(
                Optional.ofNullable(capProperties.getChallengeSize())
                        .orElse(DEFAULT_CHALLENGE_SIZE)
        );
        capProperties.setChallengeDifficulty(
                Optional.ofNullable(capProperties.getChallengeDifficulty())
                        .orElse(DEFAULT_CHALLENGE_DIFFICULTY)
        );
        capProperties.setChallengeExpiresMs(
                Optional.ofNullable(capProperties.getChallengeExpiresMs())
                        .orElse(DEFAULT_CHALLENGE_EXPIRES_MS)
        );
        capProperties.setTokenExpiresMs(
                Optional.ofNullable(capProperties.getTokenExpiresMs())
                        .orElse(DEFAULT_TOKEN_EXPIRES_MS)
        );
        capProperties.setIdSize(
                Optional.ofNullable(capProperties.getIdSize())
                        .orElse(DEFAULT_ID_SIZE)
        );
        capProperties.setTokenKeySplitter(
                Optional.ofNullable(capProperties.getTokenKeySplitter())
                        .filter(tokenKeySplitter -> !tokenKeySplitter.isBlank())
                        .orElse(DEFAULT_TOKEN_KEY_SPLITTER)
        );
    }

    private void logCapProperties(CapProperties capProperties) {
        if (log.isDebugEnabled()) {
            final var capPropertiesMap = Map.of(
                    "challengeCount", capProperties.getChallengeCount(),
                    "challengeSize", capProperties.getChallengeSize(),
                    "challengeDifficulty", capProperties.getChallengeDifficulty(),
                    "challengeExpiresMs", capProperties.getChallengeExpiresMs(),
                    "tokenExpiresMs", capProperties.getTokenExpiresMs(),
                    "idSize", capProperties.getIdSize(),
                    "tokenKeySplitter", capProperties.getTokenKeySplitter()
            );
            log.debug("cap 属性配置: {}", capPropertiesMap);
        }
    }

    @Bean("capStore")
    @ConditionalOnMissingClass({
            "top.wuhunyu.cap.server.extra.store.RedisStore"
    })
    @ConditionalOnMissingBean(name = "capStore")
    public CapStore capStore(DateHandler dateHandler) {
        return new InMemoryStore(dateHandler);
    }

    @Bean("capHandler")
    @ConditionalOnMissingBean(CapHandler.class)
    public CapHandler capHandler(
            CapProperties capProperties,
            CapStore capStore,
            DateHandler dateHandler
    ) {
        return new CapHandler(capProperties, capStore, dateHandler);
    }

    @Bean("capServer")
    @ConditionalOnMissingBean(CapServer.class)
    public CapServer capServer(CapHandler capHandler) {
        return new CapServer(capHandler);
    }

}
