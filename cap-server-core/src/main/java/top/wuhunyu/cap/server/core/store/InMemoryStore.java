package top.wuhunyu.cap.server.core.store;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import top.wuhunyu.cap.server.core.handler.DateHandler;
import top.wuhunyu.cap.server.core.model.Challenge;
import top.wuhunyu.cap.server.core.properties.StoreType;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存存储
 *
 * @author wuhunyu
 * @date 2025/06/16 16:11
 **/

@RequiredArgsConstructor
public class InMemoryStore implements CapStore {

    private final Map<String, Challenge> challengeMap = new ConcurrentHashMap<>();

    private final Map<String, Instant> tokenMap = new ConcurrentHashMap<>();

    private final DateHandler dateHandler;

    @Override
    public StoreType storeType() {
        return StoreType.IN_MEMORY;
    }

    @Override
    public boolean putChallenge(@NonNull final String token, @NonNull final Challenge challenge) {
        this.cleanChallenge(dateHandler.now());

        challengeMap.put(token, challenge);
        return true;
    }

    @Override
    public Challenge removeChallenge(@NonNull final String token) {
        this.cleanChallenge(dateHandler.now());

        return challengeMap.remove(token);
    }

    @Override
    public Challenge getChallenge(@NonNull final String token) {
        return challengeMap.get(token);
    }

    @Override
    public boolean putToken(@NonNull final String token, @NonNull final Instant expires) {
        this.cleanToken(dateHandler.now());

        tokenMap.put(token, expires);
        return true;
    }

    @Override
    public Instant removeToken(@NonNull final String token) {
        this.cleanToken(dateHandler.now());

        return tokenMap.remove(token);
    }

    @Override
    public Instant getToken(@NonNull final String token) {
        return tokenMap.get(token);
    }

    @Override
    public void clean() {
        // 获取当前日期时间
        final var now = dateHandler.now();

        this.cleanChallenge(now);
        this.cleanToken(now);
    }

    private void cleanChallenge(
            Instant now
    ) {
        final var challengeIterator = challengeMap.entrySet()
                .iterator();
        while (challengeIterator.hasNext()) {
            final var challenge = challengeIterator.next()
                    .getValue();
            if (challenge.getExpires().isBefore(now)) {
                challengeIterator.remove();
            }
        }
    }

    private void cleanToken(
            Instant now
    ) {
        final var tokenIterator = tokenMap.entrySet()
                .iterator();
        while (tokenIterator.hasNext()) {
            final var expires = tokenIterator.next()
                    .getValue();
            if (expires.isBefore(now)) {
                tokenIterator.remove();
            }
        }
    }

}
