package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.PlatformAssetEntity;
import com.mc.payment.core.service.mapper.PlatformAssetMapper;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.platform.PlatformAssetListReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetPageReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetSaveReq;
import com.mc.payment.core.service.service.PlatformAssetService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_platform_asset(平台资产)】的数据库操作Service实现
 * @createDate 2024-11-04 14:21:48
 */
@Service
public class PlatformAssetServiceImpl extends ServiceImpl<PlatformAssetMapper, PlatformAssetEntity>
        implements PlatformAssetService {
    @Override
    public BasePageRsp<PlatformAssetEntity> selectPage(PlatformAssetPageReq req) {
        Page<PlatformAssetEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.lambdaQuery()
                .likeRight(StrUtil.isNotBlank(req.getId()), PlatformAssetEntity::getId, req.getId())
                .eq(req.getAssetType() != null, PlatformAssetEntity::getAssetType, req.getAssetType())
                .like(StrUtil.isNotBlank(req.getAssetName()), PlatformAssetEntity::getAssetName, req.getAssetName())
                .eq(req.getStatus() != null, PlatformAssetEntity::getStatus, req.getStatus())
                .orderByDesc(BaseNoLogicalDeleteEntity::getUpdateTime)
                .page(page);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public String save(PlatformAssetSaveReq req) {
        boolean exists = this.lambdaQuery().eq(PlatformAssetEntity::getAssetType, req.getAssetType())
                .eq(PlatformAssetEntity::getAssetName, req.getAssetName())
                .exists();
        if (exists) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST);
        }
        PlatformAssetEntity entity = req.convert();
        try {
            this.save(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST);
        }
        return entity.getId();
    }

    @Override
    public List<PlatformAssetEntity> list(PlatformAssetListReq req) {
        return this.lambdaQuery().eq(req.getAssetType() != null, PlatformAssetEntity::getAssetType, req.getAssetType())
                .like(StrUtil.isNotBlank(req.getAssetName()), PlatformAssetEntity::getAssetName, req.getAssetName())
                .eq(req.getStatus() != null, PlatformAssetEntity::getStatus, req.getStatus())
                .orderByDesc(BaseNoLogicalDeleteEntity::getCreateTime)
                .list();
    }

    /**
     * 查询是否激活
     *
     * @param assetType
     * @param assetName
     * @return
     */
    @Override
    public boolean isActivated(Integer assetType, String assetName) {
        return this.lambdaQuery().eq(PlatformAssetEntity::getAssetType, assetType)
                .eq(PlatformAssetEntity::getAssetName, assetName)
                .eq(PlatformAssetEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .exists();
    }
}




