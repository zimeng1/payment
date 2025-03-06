package com.mc.payment.core.service.model.req.merchant;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Validator;
import com.mc.payment.core.service.model.dto.MerchantChannelAssetDto;
import com.mc.payment.core.service.model.enums.BooleanStatusEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MerchantConfigUpdateReq {
    @Schema(title = "商户id")
    @NotBlank(message = "[商户id]不能为空")
    private String id;

    @Schema(title = "ip白名单,[英文逗号隔开]", description = "个数不能超过100")
    @NotBlank(message = "[ip白名单]不能为空")
    private String ipWhitelist;

    @Schema(title = "告警邮箱,[英文逗号隔开]", description = "个数不能超过20")
    @NotBlank(message = "[ip白名单]不能为空")
    private String alarmEmail;

    @Schema(title = "支持通道")
    @NotNull(message = "[支持通道]不能为空")
    private List<Integer> channelList;

    @Schema(title = "通道支持的资产")
    @NotNull(message = "[通道支持的资产]不能为空")
    @Valid
    private List<MerchantChannelAssetDto> channelAssetList;

    public void validate() {
        String[] ips = ipWhitelist.split(",");
        if (ips.length > 100) {
            throw new ValidateException("[ip白名单]个数不能超过100");
        }
        for (String ip : ips) {
            if (!Validator.isIpv4(ip) && !Validator.isIpv6(ip)) {
                throw new ValidateException("[ip白名单]格式错误,ip:" + ip);
            }
        }

        String[] emails = alarmEmail.split(",");
        if (emails.length > 20) {
            throw new ValidateException("[告警邮箱]个数不能超过20");
        }
        for (String email : emails) {
            if (!Validator.isEmail(email)) {
                throw new ValidateException("[告警邮箱]格式错误,邮箱:" + email);
            }
        }
        //  验证通道和资产  出入金状态不能全为不可用
        for (MerchantChannelAssetDto merchantChannelAssetDto : this.channelAssetList) {
            if (merchantChannelAssetDto.getDepositStatus() == BooleanStatusEnum.ITEM_0.getCode()
                    && merchantChannelAssetDto.getWithdrawalStatus() == BooleanStatusEnum.ITEM_0.getCode()) {
                throw new ValidateException("[通道支持的资产]出入金状态不能全为不可用");
            }
            if (merchantChannelAssetDto.getChannelSubType() != ChannelSubTypeEnum.FIRE_BLOCKS.getCode()) {
                if (merchantChannelAssetDto.getGenerateWalletStatus() != BooleanStatusEnum.ITEM_0.getCode()) {
                    throw new ValidateException("非fireblocks通道的,生成钱包状态只能为不可用");
                } else if (merchantChannelAssetDto.getGenerateWalletQuantity() != 0) {
                    throw new ValidateException("非fireblocks通道的,生成钱包数量只能为0");
                } else if (merchantChannelAssetDto.getGenerateWalletLeQuantity() != 0) {
                    throw new ValidateException("非fireblocks通道的,生成钱包小于等于阈值只能为0");
                }
            } else {
                if (merchantChannelAssetDto.getGenerateWalletStatus() == BooleanStatusEnum.ITEM_1.getCode()) {
                    if (merchantChannelAssetDto.getGenerateWalletLeQuantity() <= 0 || merchantChannelAssetDto.getGenerateWalletLeQuantity() > 20) {
                        throw new ValidateException("生成钱包状态启用时,生成钱包小于等于阈值只能在1-20之间");
                    }
                    if (merchantChannelAssetDto.getGenerateWalletQuantity() <= 0 || merchantChannelAssetDto.getGenerateWalletQuantity() > 20) {
                        throw new ValidateException("生成钱包状态启用时,生成钱包数量只能在1-20之间");
                    }
                }
            }
        }
    }
}
