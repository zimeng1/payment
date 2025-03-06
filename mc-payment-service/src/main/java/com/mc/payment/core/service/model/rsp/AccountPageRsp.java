package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.AccountEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author conor
 * @since 2024/2/2 15:33:01
 */
@Data
@Schema(title = "账号-分页返回实体")
public class AccountPageRsp extends AccountEntity {

    private static final long serialVersionUID = 5866636858663481290L;

    @Schema(title = "账户签约的商户的名称")
    private String merchantName;



}
