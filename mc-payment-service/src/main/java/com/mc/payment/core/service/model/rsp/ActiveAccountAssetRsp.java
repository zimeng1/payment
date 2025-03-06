package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Marty
 * @since 2024/5/23 17:13
 */
@Data
public class ActiveAccountAssetRsp implements Serializable {

    @Schema(title = "钱包地址")
    private String address;

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    private Integer accountType;

    @Schema(title = "出入金-次数")
    private Integer count = 0;

    @Schema(title = "账号id")
    private String accountId;

    @Schema(title ="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    public ActiveAccountAssetRsp() {

    }
    public ActiveAccountAssetRsp(String address, Integer accountType, Integer count, String accountId, Date createTime) {
        this.address = address;
        this.accountType = accountType;
        this.count = count;
        this.accountId = accountId;
        this.createTime = createTime;
    }

}
