package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.AssetBankEntity;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_asset_bank(资产支持的银行)】的数据库操作Service
 * @createDate 2024-07-31 19:04:33
 */
public interface AssetBankService extends IService<AssetBankEntity> {
    /**
     * 判断入金时资产是否需要银行编号
     *
     * @param assetName
     * @param netProtocol
     * @return
     */
    boolean existDeposit(String assetName, String netProtocol);

    /**
     * 判断该银行编码是否存在于这个资产的入金银行列表中
     *
     * @param assetName
     * @param netProtocol
     * @param bankCode
     * @return
     */
    boolean existDepositBankCode(String assetName, String netProtocol, String bankCode);

    /**
     * 判断该银行编码是否存在于这个资产的出金银行列表中
     *
     * @param assetName
     * @param netProtocol
     * @param bankCode
     * @return
     */
    boolean existWithdrawBankCode(String assetName, String netProtocol, String bankCode);

    /**
     * 获取入金银行列表
     *
     * @param assetName
     * @param netProtocol
     * @return
     */
    List<AssetBankEntity> getDepositBankList(String assetName, String netProtocol);

    /**
     * 判断出金时资产是否需要银行编号
     *
     * @param assetName
     * @param netProtocol
     * @return
     */
    boolean existWithdraw(String assetName, String netProtocol);

    /**
     * 获取出金银行列表
     *
     * @param assetName
     * @param netProtocol
     * @return
     */
    List<AssetBankEntity> getWithdrawBankList(String assetName, String netProtocol);

}
