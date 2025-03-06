package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.req.MerchantAuditReq;
import com.mc.payment.core.service.model.req.MerchantPageReq;
import com.mc.payment.core.service.model.req.MerchantSaveReq;
import com.mc.payment.core.service.model.req.MerchantUpdateReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigPageReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigUpdateReq;
import com.mc.payment.core.service.model.req.merchant.MerchantListReq;
import com.mc.payment.core.service.model.rsp.MerchantPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigGetByIdRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantListRsp;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
public interface IMerchantService extends IService<MerchantEntity> {
    BasePageRsp<MerchantPageRsp> page(MerchantPageReq req);

    String save(MerchantSaveReq req);

    /**
     * 重置商户的SK
     *
     * @param id
     * @return 新的sk
     */
    String resetSK(String id);

    boolean updateById(MerchantUpdateReq req);

    /**
     * 根据ak查询商户
     *
     * @param accessKey
     * @return
     */
    MerchantEntity getByAK(String accessKey);

    /**
     * 根据渠道类型查询商户
     *
     * @param channelSubType
     * @return
     */
    List<MerchantEntity> listByChannel(int channelSubType);

    void updateDepositAudit(MerchantAuditReq req);

    void updateWithdralAudit(MerchantAuditReq req);

    BasePageRsp<MerchantConfigPageRsp> configPage(MerchantConfigPageReq req);

    MerchantConfigGetByIdRsp getConfigById(String id);

    boolean configUpdateById(MerchantConfigUpdateReq req);

    List<MerchantListRsp> currentLoginList(MerchantListReq req);
}
