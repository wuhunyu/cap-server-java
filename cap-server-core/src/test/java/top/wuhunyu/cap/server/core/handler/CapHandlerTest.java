package top.wuhunyu.cap.server.core.handler;

import org.junit.jupiter.api.Test;
import top.wuhunyu.cap.server.core.model.Challenge;
import top.wuhunyu.cap.server.core.properties.CapProperties;
import top.wuhunyu.cap.server.core.store.InMemoryStore;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CapHandlerTest {

    @Test
    void createChallengeShouldUseStandalone214TokenLength() throws Exception {
        final var dateHandler = new FixedDateHandler(Instant.parse("2026-02-23T10:00:00Z"));
        final var capHandler = new CapHandler(defaultProperties(), new InMemoryStore(dateHandler), dateHandler);

        final Challenge challenge = capHandler.createChallenge();

        assertNotNull(challenge.getToken());
        assertEquals(50, challenge.getToken().length(), "challenge token length should align with standalone@2.1.4");
    }

    @Test
    void redeemChallengeShouldAcceptZeroSolutionWhenDifficultyIsZero() throws Exception {
        final var dateHandler = new FixedDateHandler(Instant.parse("2026-02-23T10:00:00Z"));
        final var properties = defaultProperties();
        properties.setChallengeDifficulty(0);

        final var capHandler = new CapHandler(properties, new InMemoryStore(dateHandler), dateHandler);
        final var challenge = capHandler.createChallenge();

        final var token = capHandler.redeemChallenge(challenge.getToken(), List.of(0L));

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(47, token.getToken().length(), "token format should be <16 hex id>:<30 hex vertoken>");
    }

    private CapProperties defaultProperties() {
        return CapProperties.builder()
                .challengeCount(1)
                .challengeSize(32)
                .challengeDifficulty(1)
                .challengeExpiresMs(60_000L)
                .tokenExpiresMs(1_200_000L)
                .idSize(16L)
                .tokenKeySplitter(":")
                .build();
    }

    private static final class FixedDateHandler extends DateHandler {

        private final Instant fixedNow;

        private FixedDateHandler(Instant fixedNow) {
            this.fixedNow = fixedNow;
        }

        @Override
        public Instant now() {
            return fixedNow;
        }
    }
}
