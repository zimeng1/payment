package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.PayProtocolEntity;
import com.mc.payment.core.service.model.req.platform.*;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_pay_protocol(支付协议数据表(法币-支付类型/加密货币-网络协议))】的数据库操作Service
 * @createDate 2024-11-04 17:01:04
 */
public interface PayProtocolService extends IService<PayProtocolEntity> {

    BasePageRsp<PayProtocolEntity> selectFiatPage(FiatPayTypePageReq req);

    String fiatSave(FiatPayTypeSaveReq req);

    BasePageRsp<PayProtocolEntity> selectCryptoPage(CryptoProtocolPageReq req);

    String cryptoSave(CryptoProtocolSaveReq req);

    List<PayProtocolEntity> list(PayProtocolListReq req);

    /**
     * 查询是否启用
     *
     * @param assetType
     * @param netProtocol
     * @return
     */
    boolean isActivated(Integer assetType, String netProtocol);

    /**
     * 根据网络协议和地址校验是否匹配
     * <p>
     * 若无匹配的正则表达式配置，则返回true
     *
     * @param netProtocol
     * @param address
     * @return
     */
    boolean checkAddressMatches(String netProtocol, String address);
}
