package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.entity.MerchantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/4/20 上午11:01
 */
@Data
@Builder
@Schema(title = "当前商户信息")
public class CurrentMerchantDto {
    @Schema(title = "商户id")
    private String id;

    @Schema(title = "商户名称")
    private String name;

    public static CurrentMerchantDto valueOf(MerchantEntity req) {
        return CurrentMerchantDto.builder()
                .id(req.getId())
                .name(req.getName())
                .build();

    }
}
