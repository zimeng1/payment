package com.mc.payment.gateway.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayQueryBalanceReq extends BaseGatewayReq {
    /**
     * 账户id
     */
    private String accountId;
    /**
     * 币种/代币/货币/资产
     */
    private String assetId;


}
