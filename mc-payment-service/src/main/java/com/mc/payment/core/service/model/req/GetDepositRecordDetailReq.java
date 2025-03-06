package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author taoliu
 * @date 2024-04-24 16:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDepositRecordDetailReq extends BaseReq {

    @NotBlank(message = "[id]不能为空")
    @Schema(title = "id")
    private String id;

    @Schema(title = "失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireDate;
}
