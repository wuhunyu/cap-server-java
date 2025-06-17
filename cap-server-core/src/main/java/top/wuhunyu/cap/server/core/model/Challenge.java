package top.wuhunyu.cap.server.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * 挑战
 *
 * @author wuhunyu
 * @date 2025/06/16 16:14
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge implements Serializable {

    @Serial
    private static final long serialVersionUID = -2996283225878621851L;

    private List<List<String>> challenge;

    private Instant expires;

    private String token;

}
