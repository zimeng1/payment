package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.PayProtocolEntity;
import com.mc.payment.core.service.mapper.PayProtocolMapper;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.platform.*;
import com.mc.payment.core.service.service.PayProtocolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_pay_protocol(支付协议数据表(法币-支付类型/加密货币-网络协议))】的数据库操作Service实现
 * @createDate 2024-11-04 17:01:04
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class PayProtocolServiceImpl extends ServiceImpl<PayProtocolMapper, PayProtocolEntity>
        implements PayProtocolService {
    private final AppConfig appConfig;

    @Override
    public BasePageRsp<PayProtocolEntity> selectFiatPage(FiatPayTypePageReq req) {
        Page<PayProtocolEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.lambdaQuery()
                .likeRight(StrUtil.isNotBlank(req.getId()), PayProtocolEntity::getId, req.getId())
                .eq(PayProtocolEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                .like(StrUtil.isNotBlank(req.getNetProtocol()), PayProtocolEntity::getNetProtocol, req.getNetProtocol())
                .eq(req.getStatus() != null, PayProtocolEntity::getStatus, req.getStatus())
                .orderByDesc(BaseNoLogicalDeleteEntity::getId)
                .page(page);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public String fiatSave(FiatPayTypeSaveReq req) {
        boolean exists = this.lambdaQuery()
                .eq(PayProtocolEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                .eq(PayProtocolEntity::getNetProtocol, req.getNetProtocol())
                .exists();
        if (exists) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST);
        }
        PayProtocolEntity entity = req.convert();
        try {
            this.save(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST);
        }
        return entity.getId();
    }

    @Override
    public BasePageRsp<PayProtocolEntity> selectCryptoPage(CryptoProtocolPageReq req) {
        Page<PayProtocolEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.lambdaQuery()
                .likeRight(StrUtil.isNotBlank(req.getId()), PayProtocolEntity::getId, req.getId())
                .eq(PayProtocolEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .like(StrUtil.isNotBlank(req.getNetProtocol()), PayProtocolEntity::getNetProtocol, req.getNetProtocol())
                .like(StrUtil.isNotBlank(req.getAssetNet()), PayProtocolEntity::getAssetNet, req.getAssetNet())
                .eq(req.getStatus() != null, PayProtocolEntity::getStatus, req.getStatus())
                .like(StrUtil.isNotBlank(req.getRegularExpression()), PayProtocolEntity::getRegularExpression, req.getRegularExpression())
                .orderByDesc(BaseNoLogicalDeleteEntity::getId)
                .page(page);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public String cryptoSave(CryptoProtocolSaveReq req) {
        boolean exists = this.lambdaQuery()
                .eq(PayProtocolEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(PayProtocolEntity::getNetProtocol, req.getNetProtocol())
                .eq(PayProtocolEntity::getAssetNet, req.getAssetNet())
                .exists();
        if (exists) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST);
        }
        PayProtocolEntity entity = req.convert();
        try {
            this.save(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST);
        }
        return entity.getId();
    }


    @Override
    public List<PayProtocolEntity> list(PayProtocolListReq req) {
        return this.lambdaQuery()
                .eq(req.getAssetType() != null, PayProtocolEntity::getAssetType, req.getAssetType())
                .like(StrUtil.isNotBlank(req.getNetProtocol()), PayProtocolEntity::getNetProtocol, req.getNetProtocol())
                .eq(req.getStatus() != null, PayProtocolEntity::getStatus, req.getStatus())
                .orderByDesc(BaseNoLogicalDeleteEntity::getId)
                .list();
    }

    /**
     * 查询是否启用
     *
     * @param assetType
     * @param netProtocol
     * @return
     */
    @Override
    public boolean isActivated(Integer assetType, String netProtocol) {
        return this.lambdaQuery()
                .eq(PayProtocolEntity::getAssetType, assetType)
                .eq(PayProtocolEntity::getNetProtocol, netProtocol)
                .eq(PayProtocolEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .exists();
    }

    /**
     * 根据网络协议和地址校验是否匹配
     * <p>
     * 若无匹配的正则表达式配置，则返回true
     *
     * @param netProtocol
     * @param address
     * @return
     */
    @Override
    public boolean checkAddressMatches(String netProtocol, String address) {
        if (appConfig.getWithdrawalAddressEnabled() == 1) {
            log.debug("checkAddressMatches已关闭,直接返回true,netProtocol:{},address:{}", netProtocol, address);
            return true;
        }
        PayProtocolEntity entity = this.lambdaQuery()
                .eq(PayProtocolEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(PayProtocolEntity::getNetProtocol, netProtocol)
                .one();
        if (entity != null && StrUtil.isNotBlank(entity.getRegularExpression())) {
            return address.matches(entity.getRegularExpression());
        }
        return true;
    }
}




