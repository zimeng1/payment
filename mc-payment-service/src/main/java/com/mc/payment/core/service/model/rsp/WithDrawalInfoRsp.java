package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.model.dto.AssetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author conor
 * @since 2024/7/24 下午9:43:52
 */
@Data
public class WithDrawalInfoRsp {
    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "资产名称/币种")
    private String assetName;

    @Schema(title = "网络类型/支付类型")
    private String netProtocol;

    @Schema(title = "资产网络/支付类型名称")
    private String assetNet;

    @Schema(title = "失效时间戳-精确毫秒")
    private Long expireTimestamp;

    @Schema(title = "支持的资产信息")
    private List<AssetDto> assetList;

    @Schema(title = "页面文本")
    private String pageTextJson;

    @Schema(title = "银行编号")
    private String bankCode;
}
