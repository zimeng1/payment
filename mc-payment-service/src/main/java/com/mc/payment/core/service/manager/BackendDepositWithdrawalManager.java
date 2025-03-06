package com.mc.payment.core.service.manager;

import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.api.model.rsp.QueryAssetRsp;
import com.mc.payment.api.model.rsp.QueryAssetSupportedBankRsp;
import com.mc.payment.api.model.rsp.WithdrawalRsp;
import com.mc.payment.core.service.model.req.BackendDepositRequestReq;
import com.mc.payment.core.service.model.req.BackendQueryAssetReq;
import com.mc.payment.core.service.model.req.BackendQueryAssetSupportedBankReq;
import com.mc.payment.core.service.model.req.BackendWithdrawalRequestReq;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 提供给后台管理使用的出入金功能
 * 主要是封装web端的参数,调用openapi中出入金相关的服务
 *
 * @author Conor
 * @since 2024-11-25 14:22:12.180
 */
public interface BackendDepositWithdrawalManager {
    /**
     * 查询资产
     * 1. 校验商户id是否存当前登录用户所属的商户
     * 2. 根据商户id查询ak/sk配置
     * 3. 调用openapi查询资产
     *
     * @param req
     * @return
     */
    List<QueryAssetRsp> queryAsset(BackendQueryAssetReq req);

    /**
     * 查询支持的银行
     *
     * @param req
     * @return
     */
    List<QueryAssetSupportedBankRsp> queryAssetSupportedBank(BackendQueryAssetSupportedBankReq req);

    /**
     * 入金申请
     *
     * @param req
     * @return
     */
    DepositRsp depositRequest(BackendDepositRequestReq req, HttpServletRequest request);

    /**
     * 出金申请
     *
     * @param req
     * @param request
     * @return
     */
    WithdrawalRsp withdrawalRequest(BackendWithdrawalRequestReq req, HttpServletRequest request);
}
