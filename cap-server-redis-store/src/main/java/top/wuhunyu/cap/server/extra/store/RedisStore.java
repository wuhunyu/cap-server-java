package top.wuhunyu.cap.server.extra.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import top.wuhunyu.cap.server.core.model.Challenge;
import top.wuhunyu.cap.server.core.properties.CapProperties;
import top.wuhunyu.cap.server.core.properties.StoreType;
import top.wuhunyu.cap.server.core.store.CapStore;

import java.time.Duration;
import java.time.Instant;

/**
 * redis 存储
 *
 * @author wuhunyu
 * @date 2025/06/16 23:07
 **/

@RequiredArgsConstructor
@Slf4j
public class RedisStore implements CapStore {

    public static final String CHALLENGE_KEY = "cap:challenge:";

    public static final String TOKEN_KEY = "cap:token:";

    private final RedissonClient redissonClient;

    private final CapProperties capProperties;

    @Override
    public StoreType storeType() {
        return StoreType.REDIS;
    }

    @Override
    public boolean putChallenge(final String token, final Challenge challenge) {
        redissonClient.getBucket(
                        this.makeupChallengeKey(token)
                )
                .set(
                        challenge,
                        Duration.ofMillis(capProperties.getChallengeExpiresMs())
                );
        return true;
    }

    @Override
    public Challenge removeChallenge(final String token) {
        return (Challenge) redissonClient.getBucket(
                        this.makeupChallengeKey(token)
                )
                .getAndDelete();
    }

    @Override
    public Challenge getChallenge(final String token) {
        return (Challenge) redissonClient.getBucket(
                        this.makeupChallengeKey(token)
                )
                .get();
    }

    @Override
    public boolean putToken(final String token, final Instant expires) {
        redissonClient.getBucket(
                        this.makeupTokenKey(token)
                )
                .set(
                        expires,
                        Duration.ofMillis(capProperties.getTokenExpiresMs())
                );
        return true;
    }

    @Override
    public Instant removeToken(final String token) {
        return (Instant) redissonClient.getBucket(
                        this.makeupTokenKey(token)
                )
                .getAndDelete();
    }

    @Override
    public Instant getToken(final String token) {
        return (Instant) redissonClient.getBucket(
                        this.makeupTokenKey(token)
                )
                .get();
    }

    @Override
    public void clean() {
    }

    private String makeupChallengeKey(final String token) {
        return CHALLENGE_KEY + token;
    }

    private String makeupTokenKey(final String token) {
        return TOKEN_KEY + token;
    }

}
