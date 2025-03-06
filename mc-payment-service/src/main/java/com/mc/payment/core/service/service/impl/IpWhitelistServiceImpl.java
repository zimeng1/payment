package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.IpWhitelistEntity;
import com.mc.payment.core.service.mapper.IpWhitelistMapper;
import com.mc.payment.core.service.model.req.IpWhitelistPageReq;
import com.mc.payment.core.service.model.req.IpWhitelistSaveReq;
import com.mc.payment.core.service.model.req.IpWhitelistUpdateReq;
import com.mc.payment.core.service.service.IpWhitelistService;
import com.mc.payment.core.service.util.SimpleCache;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_ip_whitelist(ip白名单)】的数据库操作Service实现
 * @createDate 2024-06-03 18:49:52
 */
@Service
public class IpWhitelistServiceImpl extends ServiceImpl<IpWhitelistMapper, IpWhitelistEntity>
        implements IpWhitelistService {
    public static final int EXPIRY_IN_SECONDS = 5 * 60;
    private final SimpleCache<String, List<String>> cache = new SimpleCache<>();

    @PostConstruct
    public void init() {
        // 从数据库中获取 IP 白名单，然后将它们放入缓存中
        cache.put("ipWhiteList", getIpWhiteListFromDB(), EXPIRY_IN_SECONDS);
    }

    /**
     * 从数据库中获取 IP 白名单
     *
     * @return
     */
    @Override
    public List<String> getIpWhiteListFromDB() {
        return list(Wrappers.lambdaQuery(IpWhitelistEntity.class)
                .eq(IpWhitelistEntity::getStatus, 1)
                .select(IpWhitelistEntity::getIpAddr))
                .stream().map(IpWhitelistEntity::getIpAddr).toList();
    }

    @Override
    public BasePageRsp<IpWhitelistEntity> page(IpWhitelistPageReq req) {
        LambdaQueryWrapper<IpWhitelistEntity> query = Wrappers.lambdaQuery(IpWhitelistEntity.class);
        if (StrUtil.isNotBlank(req.getIpAddr())) {
            query.like(IpWhitelistEntity::getIpAddr, req.getIpAddr());
        }
        if (req.getStatus() != null) {
            query.eq(IpWhitelistEntity::getStatus, req.getStatus());
        }
        if (req.getCreateTimeStart() != null) {
            query.ge(IpWhitelistEntity::getCreateTime, req.getCreateTimeStart());
        }
        if (req.getCreateTimeEnd() != null) {
            query.le(IpWhitelistEntity::getCreateTime, req.getCreateTimeEnd());
        }
        query.orderByDesc(IpWhitelistEntity::getCreateTime);

        Page<IpWhitelistEntity> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.selectPage(page, query);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public IpWhitelistEntity getOne(String ipAddr) {
        return this.getOne(Wrappers.lambdaQuery(IpWhitelistEntity.class).eq(IpWhitelistEntity::getIpAddr, ipAddr));
    }

    @Override
    public RetResult<String> save(IpWhitelistSaveReq req) {
        IpWhitelistEntity whitelistEntity = getOne(req.getIpAddr());
        if (whitelistEntity != null) {
            return RetResult.error("IP地址已存在");
        }
        IpWhitelistEntity entity = IpWhitelistEntity.valueOf(req);
        boolean b = save(entity);
        return RetResult.data(entity.getId());
    }

    @Override
    public RetResult<Boolean> updateById(IpWhitelistUpdateReq req) {
        IpWhitelistEntity whitelistEntity = getById(req.getId());
        if (whitelistEntity == null) {
            return RetResult.error("数据不存在");
        }
        boolean b = updateById(IpWhitelistEntity.valueOf(req));
        return RetResult.data(b);
    }

    /**
     * 获取所有的白名单IP
     *
     * @return
     */
    @Override
    public List<String> getIpWhiteList() {
        List<String> list = cache.get("ipWhiteList");
        if (list == null) {
            list = getIpWhiteListFromDB();
            cache.put("ipWhiteList", list, EXPIRY_IN_SECONDS);
        }
        return list;
    }

}




