package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.DictEntity;
import com.mc.payment.core.service.mapper.DictMapper;
import com.mc.payment.core.service.model.req.DictPageReq;
import com.mc.payment.core.service.model.req.DictQueryReq;
import com.mc.payment.core.service.model.req.DictSaveReq;
import com.mc.payment.core.service.model.req.DictUpdateReq;
import com.mc.payment.core.service.model.rsp.DictCategoryDescRsp;
import com.mc.payment.core.service.model.rsp.DictRsp;
import com.mc.payment.core.service.service.IDictService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;





/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-23 11:18:13
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, DictEntity> implements IDictService {

    @Override
    public List<DictRsp> dictQuery(DictQueryReq req) {
        // 沒有传递参数就直接返回全部.
        LambdaQueryWrapper<DictEntity> query = Wrappers.lambdaQuery(DictEntity.class);
        if (StrUtil.isNotBlank(req.getCategoryCode())) {
            query.eq(DictEntity::getCategoryCode, req.getCategoryCode());
        }
        if (StrUtil.isNotBlank(req.getParentCode())) {
            query.eq(DictEntity::getParentCode, req.getParentCode());
        }
        query.eq(DictEntity::getDeleted, 0);
        query.orderByAsc(DictEntity::getSortNo);
        List<DictEntity> dictEntityList = baseMapper.selectList(query);
        if (CollUtil.isEmpty(dictEntityList)) {
            return new ArrayList<>();
        }
        List<DictRsp> rspList = dictEntityList.stream().map(item -> {
            DictRsp rsp = new DictRsp();
            rsp.setParentCode(item.getParentCode());
            rsp.setDictCode(item.getDictCode());
            rsp.setDictDesc(item.getDictDesc());
            rsp.setCategoryCode(item.getCategoryCode());
            rsp.setCategoryDesc(item.getCategoryDesc());
            rsp.setAttributes(item.getAttributes());
            return rsp;
        }).toList();
        return rspList;
    }


    @Override
    public List<DictCategoryDescRsp> dictCategoryCodeQuery() {
        List<DictCategoryDescRsp> dictEntityList = baseMapper.dictCategoryCodeQuery();
        if (CollUtil.isEmpty(dictEntityList)) {
            return new ArrayList<>();
        }
        return dictEntityList;
    }

    @Override
    public BasePageRsp<DictEntity> page(DictPageReq req) {
        LambdaQueryWrapper<DictEntity> query = Wrappers.lambdaQuery(DictEntity.class);
        if (StrUtil.isNotBlank(req.getDictCode())) {
            query.eq(DictEntity::getDictCode, req.getDictCode());
        }
        if (StrUtil.isNotBlank(req.getCategoryCode())) {
            query.eq(DictEntity::getCategoryCode, req.getCategoryCode());
        }
        if (StrUtil.isNotBlank(req.getParentCode())) {
            query.eq(DictEntity::getParentCode, req.getParentCode());
        }
        if (req.getUpdateTimeStart() != null) {
            query.ge(DictEntity::getUpdateTime, req.getUpdateTimeStart());
        }
        if (req.getUpdateTimeEnd() != null) {
            query.le(DictEntity::getUpdateTime, req.getUpdateTimeEnd());
        }
        query.eq(DictEntity::getDeleted, 0);
        query.orderByAsc(DictEntity::getSortNo);

        Page<DictEntity> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.selectPage(page, query);

        return BasePageRsp.valueOf(page);
    }

    @Override
    public RetResult<String> save(DictSaveReq req) {
        //  编码 + 分类 唯一
        LambdaQueryWrapper<DictEntity> query = Wrappers.lambdaQuery(DictEntity.class);
        query.eq(DictEntity::getCategoryCode, req.getCategoryCode());
        query.eq(DictEntity::getDictCode, req.getDictCode());

        if (baseMapper.selectCount(query) > 0) {
            String error = String.format("DictCode=%s, CategoryCode=%s, 该编码已存在", req.getDictCode(), req.getCategoryCode());
            return RetResult.error(error);
        }
        DictEntity entity = DictEntity.valueOf(req);
        baseMapper.insert(entity);
        return RetResult.data(entity.getId());
    }

    @Override
    public Boolean updateById(DictUpdateReq req) {
        DictEntity entity = DictEntity.valueOf(req);
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean checkExist(DictQueryReq req) {
        //分类编码+编码 才能确认唯一. 没有就失败
        if (StrUtil.isBlank(req.getCategoryCode()) || StrUtil.isBlank(req.getDictCode())) {
            return false;
        }
        LambdaQueryWrapper<DictEntity> query = Wrappers.lambdaQuery(DictEntity.class);
        query.eq(DictEntity::getCategoryCode, req.getCategoryCode());
        query.eq(DictEntity::getDictCode, req.getDictCode());
        query.eq(DictEntity::getDeleted, 0);
        DictEntity entity = baseMapper.selectOne(query);
        return entity != null;
    }
}
