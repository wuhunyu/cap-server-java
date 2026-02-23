package top.wuhunyu.cap.server.core.handler;

import lombok.RequiredArgsConstructor;
import top.wuhunyu.cap.server.core.exception.ChallengeStoreException;
import top.wuhunyu.cap.server.core.model.Challenge;
import top.wuhunyu.cap.server.core.model.ChallengeDetail;
import top.wuhunyu.cap.server.core.model.Token;
import top.wuhunyu.cap.server.core.properties.CapProperties;
import top.wuhunyu.cap.server.core.store.CapStore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * cap 处理器
 *
 * @author wuhunyu
 * @date 2025/06/16 16:22
 **/

@RequiredArgsConstructor
public class CapHandler {

    // 十六进制字符串
    public static final String HEX_STR = "0123456789abcdef";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CapProperties capProperties;

    private final CapStore capStore;

    private final DateHandler dateHandler;

    public Challenge createChallenge() throws ChallengeStoreException {
        final var challengeCount = capProperties.getChallengeCount();
        final var challengeSize = capProperties.getChallengeSize();
        final var challengeDifficulty = capProperties.getChallengeDifficulty();
        final var challengeExpiresMs = capProperties.getChallengeExpiresMs();

        // 生成 token
        final var token = UUID.randomUUID().toString().replace("-", "");

        // 挑战过期时间
        final var expires = dateHandler.now()
                .plus(challengeExpiresMs, ChronoUnit.MILLIS);

        // 构建挑战对象
        final var challenge = Challenge.builder()
                .challenge(ChallengeDetail.builder()
                        .c(challengeCount)
                        .s(challengeSize)
                        .d(challengeDifficulty)
                        .build())
                .token(token)
                .expires(expires)
                .build();

        // 存储挑战
        if (!capStore.putChallenge(
                token,
                challenge
        )) {
            throw new ChallengeStoreException("Storage challenge failed, please try again later.");
        }

        return challenge;
    }

    public Token redeemChallenge(
            String token,
            List<Long> solutions
    ) throws IllegalArgumentException, IllegalStateException, ChallengeStoreException {
        if (isBlank(token) || solutions == null || solutions.isEmpty()) {
            throw new IllegalArgumentException("Invalid body");
        }
        // 当前日期时间
        final var now = dateHandler.now();

        // 移除 token
        final var challenge = capStore.removeChallenge(token);
        if (Objects.isNull(challenge) || !challenge.getExpires().isAfter(now)) {
            throw new IllegalStateException("Challenge expired");
        }

        // 生成挑战
        final var c = challenge.getChallenge().getC();
        final var s = challenge.getChallenge().getS();
        final var d = challenge.getChallenge().getD();
        if (solutions.size() != c) {
            throw new IllegalArgumentException("Solution not enough");
        }
        final var challenges = IntStream.range(1, c + 1)
                .boxed()
                .map(i ->
                        new AbstractMap.SimpleImmutableEntry<>(
                                top.wuhunyu.cap.server.core.utils.RandomUtil.prng(
                                        String.format("%s%d", token, i),
                                        s
                                ),
                                top.wuhunyu.cap.server.core.utils.RandomUtil.prng(
                                        String.format("%s%dd", token, i),
                                        d
                                )
                        ))
                .toList();

        // 验证计算结果是否有效
        final var n = challenges.size();
        var isValid = true;
        for (var i = 0; i < n; i++) {
            final var solution = solutions.get(i);
            if (solution == 0) {
                isValid = false;
                break;
            }

            final var pair = challenges.get(i);
            final var salt = pair.getKey();
            final var target = pair.getValue();
            if (!sha256Hex(salt + solution)
                    .startsWith(target)) {
                isValid = false;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalStateException("Invalid solution");
        }

        // 保存 token，用于后续验证
        final var verToken = UUID.randomUUID().toString().replace("-", "");
        final var expires = now.plus(capProperties.getTokenExpiresMs(), ChronoUnit.MILLIS);
        final var hash = sha256Hex(verToken);
        final var id = randomString(HEX_STR, ((int) capProperties.getIdSize().longValue()));
        if (!capStore.putToken(
                this.makeupToken(id, hash),
                expires
        )) {
            throw new ChallengeStoreException("Storage token failed, please try again later.");
        }

        // 构造验证凭证
        return Token.builder()
                .token(this.makeupVerToken(id, verToken))
                .expires(expires)
                .build();
    }

    private String makeupToken(String id, String hash) {
        return String.format("%s%s%s", id, capProperties.getTokenKeySplitter(), hash);
    }

    private String makeupVerToken(String id, String verToken) {
        return String.format("%s%s%s", id, capProperties.getTokenKeySplitter(), verToken);
    }

    public Boolean validateToken(
            String tokenStr
    ) throws IllegalArgumentException {
        if (isBlank(tokenStr) ||
                !tokenStr.contains(capProperties.getTokenKeySplitter())) {
            throw new IllegalArgumentException("Invalid body");
        }

        // 当前日期时间
        final var now = dateHandler.now();

        // 提取 id 和 verToken
        final var splits = tokenStr.split(capProperties.getTokenKeySplitter(), 2);
        final var id = splits[0];
        final var verToken = splits[1];
        final var hash = sha256Hex(verToken);
        final var tokenKey = this.makeupToken(id, hash);

        // 取出 token 的过期时间
        final var expires = capStore.removeToken(tokenKey);
        return Objects.nonNull(expires) && !expires.isBefore(now);
    }

    private String sha256Hex(String value) {
        try {
            final var digest = MessageDigest.getInstance("SHA-256");
            final var bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            final var builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
        }
    }

    private String randomString(String source, int length) {
        if (source == null || source.isEmpty() || length <= 0) {
            throw new IllegalArgumentException("Invalid random string args");
        }
        final var builder = new StringBuilder(length);
        for (var i = 0; i < length; i++) {
            builder.append(source.charAt(SECURE_RANDOM.nextInt(source.length())));
        }
        return builder.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
