package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Create a new vault account
 * @author Marty
 * @since 2024/04/13 15:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
//Create a new vault account")
public class CreateAccountReq extends BaseReq {
    //帐户名称, 必须要非中文
    //Account Name")
    @NotBlank(message = "[Account Name] is null")
    private String name;

    //是否隐藏， 如果为 true，则创建的帐户和所有相关交易将不会显示在 Fireblocks 控制台上
    //Optional - if true, the created account and all related transactions will not be shown on Fireblocks console")
    private Boolean hiddenOnUI = false;

    //客户参考 ID
    //Optional - Sets a customer reference ID")
    private String customerRefId;

    //保管库帐户的 autoFuel 属性
    //Optional - Sets the autoFuel property of the vault account")
    private Boolean autoFuel;


}
