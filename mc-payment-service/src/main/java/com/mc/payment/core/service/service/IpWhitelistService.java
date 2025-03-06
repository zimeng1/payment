package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.IpWhitelistEntity;
import com.mc.payment.core.service.model.req.IpWhitelistPageReq;
import com.mc.payment.core.service.model.req.IpWhitelistSaveReq;
import com.mc.payment.core.service.model.req.IpWhitelistUpdateReq;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_ip_whitelist(ip白名单)】的数据库操作Service
 * @createDate 2024-06-03 18:49:52
 */
public interface IpWhitelistService extends IService<IpWhitelistEntity> {
    List<String> getIpWhiteListFromDB();

    BasePageRsp<IpWhitelistEntity> page(IpWhitelistPageReq req);

    IpWhitelistEntity getOne(String ipAddr);

    RetResult<String> save(IpWhitelistSaveReq req);


    RetResult<Boolean> updateById(IpWhitelistUpdateReq req);

    /**
     * 获取所有的白名单IP
     *
     * @return
     */
    List<String> getIpWhiteList();
}
