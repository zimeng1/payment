package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryDepositReportReq {

    @Schema(title = "商户跟踪id集合",description = "各个商户的每次交易操作应保证唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "[商户跟踪id集合]不能为空")
    private List<String> trackingIdList;

}
