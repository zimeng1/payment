package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.MerchantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author conor
 * @since 2024/2/1 11:11:18
 */
@Data
@Schema(title = "商户-分页返回实体")
public class MerchantPageRsp extends MerchantEntity {
    private static final long serialVersionUID = -1560313421823133299L;

/*    @Schema(title = "通道名称集合")
    private String channelNameDescs;

    @Schema(title = "业务范围名称集合")
    private String businessScopeDescs;*/

}
