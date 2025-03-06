package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.ProtocolConfigEntity;
import com.mc.payment.core.service.mapper.ProtocolConfigMapper;
import com.mc.payment.core.service.model.req.ProtocolConfigReq;
import com.mc.payment.core.service.service.ProtocolConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @author Conor
 * @description 针对表【mcp_protocol_config(协议钱包地址正则表达式配置表)】的数据库操作Service实现
 * @createDate 2024-07-05 14:11:41
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProtocolConfigServiceImpl extends ServiceImpl<ProtocolConfigMapper, ProtocolConfigEntity>
        implements ProtocolConfigService {
    private final AppConfig appConfig;

    // 新增数据
    @Override
    public boolean add(ProtocolConfigReq req) {
        if (StringUtils.isBlank(req.getNetProtocol()) || StringUtils.isBlank(req.getRegularExpression())) {
            throw new IllegalArgumentException("网络类型/正则表达式不能为空！");
        }
        try {
            return save(ProtocolConfigEntity.valueOf(req));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("不能保存重复网络类型！");
        }
    }

    // 根据 ID 删除数据
    @Override
    public boolean delete(String id) {
        return removeById(id);
    }

    // 更新数据
    @Override
    public boolean update(ProtocolConfigReq req) {
        try {
            return updateById(ProtocolConfigEntity.valueOf(req));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("不能保存重复网络类型！");
        }
    }

    // 根据条件查询数据
    @Override
    public BasePageRsp<ProtocolConfigEntity> page(ProtocolConfigReq req) {
        Page<ProtocolConfigEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.page(page, Wrappers.lambdaQuery(ProtocolConfigEntity.class)
                .eq(StrUtil.isNotBlank(req.getNetProtocol()), ProtocolConfigEntity::getNetProtocol, req.getNetProtocol())
                .eq(StrUtil.isNotBlank(req.getRegularExpression()), ProtocolConfigEntity::getRegularExpression, req.getRegularExpression())
                .orderByDesc(ProtocolConfigEntity::getCreateTime));
        return BasePageRsp.valueOf(page);
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
        ProtocolConfigEntity protocolConfigEntity = getOne(Wrappers.lambdaQuery(ProtocolConfigEntity.class)
                .eq(ProtocolConfigEntity::getNetProtocol, netProtocol));
        if (protocolConfigEntity != null) {
            return address.matches(protocolConfigEntity.getRegularExpression());
        }
        return true;
    }
}




