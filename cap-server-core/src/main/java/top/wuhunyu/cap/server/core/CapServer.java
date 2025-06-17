package top.wuhunyu.cap.server.core;

import lombok.RequiredArgsConstructor;
import top.wuhunyu.cap.server.core.exception.ChallengeStoreException;
import top.wuhunyu.cap.server.core.handler.CapHandler;
import top.wuhunyu.cap.server.core.request.RedeemChallengeRequest;
import top.wuhunyu.cap.server.core.response.ChallengeResponse;
import top.wuhunyu.cap.server.core.response.RedeemChallengeResponse;

/**
 * cap 服务端
 *
 * @author wuhunyu
 * @date 2025/06/16 16:08
 **/

@RequiredArgsConstructor
public class CapServer {

    private final CapHandler capHandler;

    public ChallengeResponse createChallenge() throws ChallengeStoreException {
        final var challenge = capHandler.createChallenge();
        return ChallengeResponse.builder()
                .challenge(challenge.getChallenge())
                .expires(challenge.getExpires())
                .token(challenge.getToken())
                .build();
    }

    public RedeemChallengeResponse redeemChallenge(final RedeemChallengeRequest redeemChallengeRequest) {
        try {
            final var token = capHandler.redeemChallenge(
                    redeemChallengeRequest.getToken(),
                    redeemChallengeRequest.getSolutions()
            );
            return RedeemChallengeResponse.builder()
                    .success(true)
                    .token(token.getToken())
                    .expires(token.getExpires())
                    .build();
        } catch (IllegalArgumentException | IllegalStateException | ChallengeStoreException e) {
            return RedeemChallengeResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    public Boolean validateToken(final String token) {
        return capHandler.validateToken(token);
    }

}
