package top.wuhunyu.cap.server.core.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

/**
 * Instant 序列化 时间戳 器
 *
 * @author wuhunyu
 * @date 2025/06/16 21:17
 **/

public class Instant2TimeStampSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(
            final Instant instant,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider
    ) throws IOException {
        if (Objects.isNull(instant)) {
            jsonGenerator.writeNull();
            return;
        }
        jsonGenerator.writeNumber(instant.toEpochMilli());
    }
}
