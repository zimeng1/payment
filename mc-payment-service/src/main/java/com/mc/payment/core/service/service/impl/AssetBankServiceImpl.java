package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.AssetBankEntity;
import com.mc.payment.core.service.mapper.AssetBankMapper;
import com.mc.payment.core.service.service.AssetBankService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_asset_bank(资产支持的银行)】的数据库操作Service实现
 * @createDate 2024-07-31 19:04:33
 */
@Service
public class AssetBankServiceImpl extends ServiceImpl<AssetBankMapper, AssetBankEntity>
        implements AssetBankService {
    /**
     * 判断入金时资产是否需要银行编号
     *
     * @param assetName
     * @param netProtocol
     * @return
     */
    @Override
    public boolean existDeposit(String assetName, String netProtocol) {
        return this.exists(Wrappers.lambdaQuery(AssetBankEntity.class)
                .apply("FIND_IN_SET(0,payment_type) > 0")
                .eq(AssetBankEntity::getAssetName, assetName)
                .eq(AssetBankEntity::getNetProtocol, netProtocol));
    }

    /**
     * 判断该银行编码是否存在于这个资产的入金银行列表中
     *
     * @param assetName
     * @param netProtocol
     * @param bankCode
     * @return
     */
    @Override
    public boolean existDepositBankCode(String assetName, String netProtocol, String bankCode) {
        return this.exists(Wrappers.lambdaQuery(AssetBankEntity.class)
                .apply("FIND_IN_SET(0,payment_type) > 0")
                .eq(AssetBankEntity::getAssetName, assetName)
                .eq(AssetBankEntity::getNetProtocol, netProtocol)
                .eq(AssetBankEntity::getBankCode, bankCode));
    }

    /**
     * 判断该银行编码是否存在于这个资产的出金银行列表中
     *
     * @param assetName
     * @param netProtocol
     * @param bankCode
     * @return
     */
    @Override
    public boolean existWithdrawBankCode(String assetName, String netProtocol, String bankCode) {
        return this.exists(Wrappers.lambdaQuery(AssetBankEntity.class)
                .apply("FIND_IN_SET(1,payment_type) > 0")
                .eq(AssetBankEntity::getAssetName, assetName)
                .eq(AssetBankEntity::getNetProtocol, netProtocol)
                .eq(AssetBankEntity::getBankCode, bankCode));
    }

    /**
     * 获取入金银行列表
     *
     * @param assetName   资产名称 传null则不作为查询条件
     * @param netProtocol 网络协议 传null则不作为查询条件
     * @return
     */
    @Override
    public List<AssetBankEntity> getDepositBankList(String assetName, String netProtocol) {
        LambdaQueryWrapper<AssetBankEntity> queryWrapper = Wrappers.lambdaQuery(AssetBankEntity.class)
                .apply("FIND_IN_SET(0,payment_type) > 0");
        if (StrUtil.isNotBlank(assetName)) {
            queryWrapper.eq(AssetBankEntity::getAssetName, assetName);
        }
        if (StrUtil.isNotBlank(netProtocol)) {
            queryWrapper.eq(AssetBankEntity::getNetProtocol, netProtocol);
        }
        return this.list(queryWrapper);
    }


    /**
     * 判断出金时资产是否需要银行编号
     *
     * @param assetName
     * @param netProtocol
     * @return
     */
    @Override
    public boolean existWithdraw(String assetName, String netProtocol) {
        return this.exists(Wrappers.lambdaQuery(AssetBankEntity.class)
                .apply("FIND_IN_SET(1,payment_type) > 0")
                .eq(AssetBankEntity::getAssetName, assetName)
                .eq(AssetBankEntity::getNetProtocol, netProtocol));
    }


    /**
     * 获取出金银行列表
     *
     * @param assetName   资产名称 传null则不作为查询条件
     * @param netProtocol 网络协议 传null则不作为查询条件
     * @return
     */
    @Override
    public List<AssetBankEntity> getWithdrawBankList(String assetName, String netProtocol) {
        LambdaQueryWrapper<AssetBankEntity> queryWrapper = Wrappers.lambdaQuery(AssetBankEntity.class)
                .apply("FIND_IN_SET(1,payment_type) > 0");
        if (StrUtil.isNotBlank(assetName)) {
            queryWrapper.eq(AssetBankEntity::getAssetName, assetName);
        }
        if (StrUtil.isNotBlank(netProtocol)) {
            queryWrapper.eq(AssetBankEntity::getNetProtocol, netProtocol);
        }
        return this.list(queryWrapper);
    }
}




