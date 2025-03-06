package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetRegisterEntity;
import com.mc.payment.core.service.mapper.ChannelAssetRegisterMapper;
import com.mc.payment.core.service.model.req.ChannelAssetRegisterPageReq;
import com.mc.payment.core.service.model.req.ChannelAssetRegisterSaveReq;
import com.mc.payment.core.service.service.IChannelAssetRegisterService;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.RegisterNewAssetReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.RegisterNewAssetVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.AssetMetadataVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.AssetOnChainVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 通道资产注册表(用于支持新币种) 服务实现类
 * </p>
 *
 * @author Marty
 * @since 2024-06-20 14:59:26
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ChannelAssetRegisterServiceImpl extends ServiceImpl<ChannelAssetRegisterMapper, ChannelAssetRegisterEntity> implements IChannelAssetRegisterService {

    private final FireBlocksAPI fireBlocksAPI;


    @Override
    public BasePageRsp<ChannelAssetRegisterEntity> page(ChannelAssetRegisterPageReq req) {
        Page<ChannelAssetRegisterEntity> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<ChannelAssetRegisterEntity> query = Wrappers.lambdaQuery(ChannelAssetRegisterEntity.class)
                .orderByDesc(ChannelAssetRegisterEntity::getCreateTime);
        if (StrUtil.isNotBlank(req.getId())) {
            query.likeRight(BaseNoLogicalDeleteEntity::getId, req.getId());
        }
        if (StrUtil.isNotBlank(req.getAssetName())) {
            query.eq(ChannelAssetRegisterEntity::getAssetName, req.getAssetName());
        }
        if (StrUtil.isNotBlank(req.getChainAddress())) {
            query.likeRight(ChannelAssetRegisterEntity::getChainAddress, req.getChainAddress());
        }
        baseMapper.selectPage(page, query);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public List<ChannelAssetRegisterEntity> getList() {
        LambdaQueryWrapper<ChannelAssetRegisterEntity> queryWrapper = Wrappers.lambdaQuery(ChannelAssetRegisterEntity.class).select(ChannelAssetRegisterEntity::getId, ChannelAssetRegisterEntity::getChannelAssetName);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public RetResult<String> registerAsset(ChannelAssetRegisterSaveReq req) {
        log.info("[registerAsset],注册新币种, 参数:{}", req);
        //先查询数据库是否存在, 在调度器中fireblocks注册新币种方法
        ChannelAssetRegisterEntity channelAssetRegisterEntity = baseMapper.selectOne(Wrappers.lambdaQuery(ChannelAssetRegisterEntity.class)
                .eq(ChannelAssetRegisterEntity::getBlockChainId, req.getBlockChainId())
                .eq(ChannelAssetRegisterEntity::getChainAddress, req.getChainAddress())
        );
        if (channelAssetRegisterEntity != null) {
            return RetResult.error("新增失败, 资产名称已存在");
        }
        // 调用fireblocks注册新币种方法
        RegisterNewAssetReq registerNewAssetReq = new RegisterNewAssetReq();
        registerNewAssetReq.setIdempotencyKey(String.format("%s_%s", req.getBlockChainId(), new Date()));
        registerNewAssetReq.setBlockchainId(req.getBlockChainId());
        registerNewAssetReq.setAddress(req.getChainAddress());
        registerNewAssetReq.setSymbol(req.getSymbol());
        RetResult<RegisterNewAssetVo> result = fireBlocksAPI.registerNewAsset(registerNewAssetReq);
        log.info("[registerAsset],fireBlocksAPI req:{},ret:{}", registerNewAssetReq, result);
        if (!result.isSuccess()) {
            if (result.getMsg().contains("The asset is already supported globally")) {
                return RetResult.error("新增失败, 该币种已支持");
            }else if (result.getMsg().contains("Invalid address, could not get asset information")) {
                return RetResult.error("新增失败, 无效地址, 无法获取资产信息");
            }else {
                log.error("[registerAsset], 新币种注册失败, req:{}, msg:{}", req, result.getMsg());
                return RetResult.error("新增失败, 请检查地址与原生资产ID是否正确");
            }
        } else {
            // 保存到数据库
            ChannelAssetRegisterEntity saveEntity = new ChannelAssetRegisterEntity();
            saveEntity.setChannelSubType(req.getChannelSubType());
            saveEntity.setAssetName(req.getAssetName());
            saveEntity.setBlockChainId(req.getBlockChainId());
            saveEntity.setChainAddress(req.getChainAddress());

            RegisterNewAssetVo data = result.getData();
            AssetMetadataVo metadata = data.getMetadata();
            AssetOnChainVo chain = data.getOnchain();
            saveEntity.setChannelAssetName(data.getLegacyId());
            saveEntity.setNetProtocol(chain.getStandard());
            saveEntity.setChainSymbol(chain.getSymbol());
            saveEntity.setChainName(chain.getName());
            int decimals = chain.getDecimals() == null ? 0 : chain.getDecimals().intValue();
            saveEntity.setDecimals(decimals);
            saveEntity.setAssetClass(data.getAssetClass());
            saveEntity.setScope(metadata.getScope());
            int insert = baseMapper.insert(saveEntity);
            if (insert <= 0) {
                return RetResult.error("新增失败");
            }
            return RetResult.data("新增成功");
        }
    }
}
