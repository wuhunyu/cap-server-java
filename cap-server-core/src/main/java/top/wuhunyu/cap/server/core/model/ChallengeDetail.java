package top.wuhunyu.cap.server.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 挑战生成配置明细
 *
 * @author wuhunyu
 * @date 2025/08/21 17:19
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = -2545778614889485441L;

    private Integer c;

    private Integer s;

    private Integer d;

}
