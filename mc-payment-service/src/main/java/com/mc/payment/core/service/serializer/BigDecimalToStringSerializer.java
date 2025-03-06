package com.mc.payment.core.service.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 序列化BigDecimal为字符串,去掉末尾的0
 *
 * @author Conor
 * @since 2024-11-13 14:33:19.222
 */
public class BigDecimalToStringSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeString("");
            return;
        }
        gen.writeString(value.stripTrailingZeros().toPlainString());
    }
}
