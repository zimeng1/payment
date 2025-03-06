package com.mc.payment.core.service.model.rsp.merchant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class MerchantConfigPageRsp {
    @Schema(title = "商户id")
    private String id;

    @Schema(title = "商户名称")
    private String name;

    @Schema(title = "ip白名单,[英文逗号隔开]")
    private String ipWhitelist;

    @Schema(title = "通道子类型集合,[通道子类型:channel_sub_type 由英文逗号隔开] 0:BlockATM,1:FireBlocks")
    private String channelSubTypes;

    @Schema(title = "支持资产")
    private Integer assetSum;

    @Schema(title = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Schema(title = "操作账号")
    private String updateBy;

    // =================== 以下为非数据库字段 ===================
    @Schema(title = "通道子类型-描述")
    public String getChannelNameDescs() {
        return ChannelSubTypeEnum.getEnumDescByString(channelSubTypes);
    }

}
