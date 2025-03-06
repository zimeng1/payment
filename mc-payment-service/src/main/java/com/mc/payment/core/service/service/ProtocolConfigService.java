package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ProtocolConfigEntity;
import com.mc.payment.core.service.model.req.ProtocolConfigReq;

/**
 * @author Conor
 * @description 针对表【mcp_protocol_config(协议钱包地址正则表达式配置表)】的数据库操作Service
 * @createDate 2024-07-05 14:11:41
 */
public interface ProtocolConfigService extends IService<ProtocolConfigEntity> {

    // 新增数据
    boolean add(ProtocolConfigReq req);

    // 根据 ID 删除数据
    boolean delete(String id);

    // 更新数据
    boolean update(ProtocolConfigReq req);

    // 根据条件查询数据
    BasePageRsp<ProtocolConfigEntity> page(ProtocolConfigReq req);

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
