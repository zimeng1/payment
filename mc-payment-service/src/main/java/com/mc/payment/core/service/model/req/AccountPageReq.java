package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author conor
 * @since 2024/2/2 15:33:57
 */
@Data
public class AccountPageReq extends BasePageReq {
    private static final long serialVersionUID = 6655136770539955551L;


    @Schema(title = "账号名称")
    private String name;

    @Schema(title = "账号id")
    private String id;

    @Schema(title = "商户id集合")
    private List<String> merchantIdList;

    @Schema(title = "通道子类型集合")
    private List<Integer> channelSubTypeList;

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    private String accountType;

}
