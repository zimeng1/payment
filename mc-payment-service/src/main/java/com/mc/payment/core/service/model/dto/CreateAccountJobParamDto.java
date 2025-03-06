package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.entity.ChannelAssetEntity;
import lombok.Data;

import java.util.List;

/**
 * 创建钱包任务参数实体
 *
 * @author Conor
 * @since 2024/5/11 下午2:28
 */
@Data
public class CreateAccountJobParamDto {
    private String merchantId;
    private String merchantName;
    private Integer accountType;
    private Integer channelSubType;
    private List<ChannelAssetEntity> supportAssetList;
}
