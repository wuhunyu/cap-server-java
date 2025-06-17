package top.wuhunyu.cap.server.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.RequiredArgsConstructor;
import top.wuhunyu.cap.server.core.exception.ChallengeStoreException;
import top.wuhunyu.cap.server.core.model.Challenge;
import top.wuhunyu.cap.server.core.model.Token;
import top.wuhunyu.cap.server.core.properties.CapProperties;
import top.wuhunyu.cap.server.core.store.CapStore;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
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

    private final CapProperties capProperties;

    private final CapStore capStore;

    private final DateHandler dateHandler;

    public Challenge createChallenge() throws ChallengeStoreException {
        final var challengeCount = capProperties.getChallengeCount();
        final var challengeSize = capProperties.getChallengeSize();
        final var challengeDifficulty = capProperties.getChallengeDifficulty();
        final var challengeExpiresMs = capProperties.getChallengeExpiresMs();

        // 随机生成挑战
        final var challenges = IntStream.range(0, challengeCount)
                .boxed()
                .map(i -> List.of(
                        RandomUtil.randomString(HEX_STR, challengeSize),
                        RandomUtil.randomString(HEX_STR, challengeDifficulty)
                ))
                .toList();

        // 生成 token
        final var token = IdUtil.fastSimpleUUID();

        // 挑战过期时间
        final var expires = dateHandler.now()
                .plus(challengeExpiresMs, ChronoUnit.MILLIS);

        // 构建挑战对象
        final var challenge = Challenge.builder()
                .challenge(challenges)
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
            List<List<Object>> solutions
    ) throws IllegalArgumentException, IllegalStateException, ChallengeStoreException {
        if (StrUtil.isBlank(token) || CollUtil.isEmpty(solutions)) {
            throw new IllegalArgumentException("Invalid body");
        }
        solutions = ListUtil.sub(solutions, 0, capProperties.getChallengeCount());

        // 当前日期时间
        final var now = dateHandler.now();

        // 移除 token
        final var challenge = capStore.removeChallenge(token);
        if (Objects.isNull(challenge) || !challenge.getExpires().isAfter(now)) {
            throw new IllegalStateException("Challenge expired");
        }

        // 验证计算结果是否有效
        boolean isValid = false;
        outer:
        for (final var challenges : challenge.getChallenge()) {
            final var salt = challenges.get(0);
            final var target = challenges.get(1);

            for (final var solution : solutions) {
                if (Objects.isNull(solution) || solution.size() != 3) {
                    throw new IllegalArgumentException("Invalid body");
                }
                final var s = solution.get(0);
                final var t = solution.get(1);
                final var ans = solution.get(2);
                if (Objects.equals(salt, s) &&
                        Objects.equals(target, t) &&
                        DigestUtil.sha256Hex(salt + ans.toString())
                                .startsWith(target)
                ) {
                    isValid = true;
                    break outer;
                }
            }
        }

        if (!isValid) {
            throw new IllegalStateException("Invalid solution");
        }

        // 保存 token，用于后续验证
        final var verToken = IdUtil.fastSimpleUUID();
        final var expires = now.plus(capProperties.getTokenExpiresMs(), ChronoUnit.MILLIS);
        final var hash = DigestUtil.sha256Hex(verToken);
        final var id = RandomUtil.randomString(HEX_STR, ((int) capProperties.getIdSize().longValue()));
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
        if (StrUtil.isBlank(tokenStr) ||
                !tokenStr.contains(capProperties.getTokenKeySplitter())) {
            throw new IllegalArgumentException("Invalid body");
        }

        // 当前日期时间
        final var now = dateHandler.now();

        // 提取 id 和 verToken
        final var splits = tokenStr.split(capProperties.getTokenKeySplitter(), 2);
        final var id = splits[0];
        final var verToken = splits[1];
        final var hash = DigestUtil.sha256Hex(verToken);
        final var tokenKey = this.makeupToken(id, hash);

        // 取出 token 的过期时间
        final var expires = capStore.removeToken(tokenKey);
        return Objects.nonNull(expires) && !expires.isBefore(now);
    }

}
