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
@Schema(description = "出金申请数据")
public class WithdrawalRequestReq {

    @Schema(title = "资产名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称]不能为空")
    private String assetName;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    @Length(max = 20, message = "[网络协议]长度不能超过20")
    private String netProtocol;

    @Schema(title = "收款地址/出金地址/目标地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[收款地址]不能为空")
    private String address;

    @Schema(title = "出金说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[出金说明]不能为空")
    private String remark;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    private BigDecimal amount;


    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 50, message = "跟踪ID长度不能超过50个字符")
    @NotBlank(message = "[跟踪id]不能为空")
    private String trackingId;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "是否自动审核,[0:否,1:是]")
    @NotNull(message = "[是否自动审核]不能为空")
    @Range(min = 0, max = 1, message = "[是否自动审核]必须为[0:否,1:是]")
    private Integer autoAudit;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户ip地址")
    private String userIp;
}
