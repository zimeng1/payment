package com.mc.payment.core.service.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * 序列化BigDecimal
 * // 默认:精确二位小数，四舍五入
 *
 * @author Marty
 * @JsonSerialize(using = BigDecimalSerializer.class)
 * private BigDecimal avgScore1;
 * <p>
 * // 精确四位小数，四舍五入(0:一个数字; ‘#’:一个数字，不包括 0)
 * @JsonFormat(pattern = "#.####", shape = Shape.STRING)    //ps: 4位"#.####", 6位"#.######", 10位"#.##########"
 * @JsonSerialize(using = BigDecimalSerializer.class)
 * private BigDecimal avgScore3;
 * @since 2024/5/20 15:04
 */
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> implements ContextualSerializer {
    protected DecimalFormat decimalFormat;

    public BigDecimalSerializer() {
    }

    public BigDecimalSerializer(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeString(BigDecimal.ZERO.toPlainString());
        } else {
            if (null != decimalFormat) {
                gen.writeString(decimalFormat.format(value));
            } else {
                // 如果value小于1, 则保留六位小数,大于1保留两位小数
                if (value.compareTo(BigDecimal.ONE) < 0) {
                    gen.writeString(value.setScale(6, BigDecimal.ROUND_HALF_UP).toPlainString());
                } else{
                    gen.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                }
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format == null) {
            return this;
        }

        if (format.hasPattern()) {
            DecimalFormat decimalFormat = new DecimalFormat(format.getPattern());
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            return new BigDecimalSerializer(decimalFormat);
        }
        return this;
    }

    protected JsonFormat.Value findFormatOverrides(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(provider.getConfig(), typeForDefaults);
        }
        return provider.getDefaultPropertyFormat(typeForDefaults);
    }


}
