package top.wuhunyu.cap.server.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.wuhunyu.cap.server.core.CapServer;
import top.wuhunyu.cap.server.core.request.RedeemChallengeRequest;
import top.wuhunyu.cap.server.core.response.ChallengeResponse;
import top.wuhunyu.cap.server.core.response.RedeemChallengeResponse;

/**
 * cap server 路由
 *
 * @author wuhunyu
 * @date 2025/06/16 20:58
 **/

@RestController
@RequestMapping("/cap-server")
@RequiredArgsConstructor
public class CapServerController {

    private final CapServer capServer;

    @PostMapping("/challenge")
    public ChallengeResponse challenge() {
        return capServer.createChallenge();
    }

    @PostMapping("/redeem")
    public RedeemChallengeResponse redeem(@RequestBody @Validated RedeemChallengeRequest redeemChallengeRequest) {
        return capServer.redeemChallenge(redeemChallengeRequest);
    }

    @GetMapping("/verify/{token}")
    public Boolean verify(@PathVariable("token") String token) {
        return capServer.validateToken(token);
    }

}
