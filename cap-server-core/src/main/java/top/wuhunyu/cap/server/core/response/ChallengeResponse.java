package top.wuhunyu.cap.server.core.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.wuhunyu.cap.server.core.model.ChallengeDetail;
import top.wuhunyu.cap.server.core.serialize.Instant2TimeStampSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * 挑战响应
 *
 * @author wuhunyu
 * @date 2025/06/16 21:24
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6524572173531919368L;

    private ChallengeDetail challenge;

    @JsonSerialize(using = Instant2TimeStampSerializer.class)
    private Instant expires;

    private String token;

}
