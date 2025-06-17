package top.wuhunyu.cap.server.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.wuhunyu.cap.server.core.serialize.Instant2TimeStampSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * token
 *
 * @author wuhunyu
 * @date 2025/06/16 18:01
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = -6931868214117599084L;

    private String token;

    @JsonSerialize(using = Instant2TimeStampSerializer.class)
    private Instant expires;

}
