package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
@Schema(title = "入金申请参数")
public class DepositRequestReq {
    @Schema(title = "资产名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称]不能为空")
    private String assetName;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    @Length(max = 20, message = "[网络协议]长度不能超过20")
    private String netProtocol;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    private BigDecimal amount;

    @Schema(title = "入金说明", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 50, message = "跟踪ID长度不能超过50个字符")
    @NotBlank(message = "[跟踪ID]不能空")
    private String trackingId;


    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户ip地址")
    private String userIp;
}
