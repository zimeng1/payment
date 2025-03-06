package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.DictEntity;
import com.mc.payment.core.service.model.req.DictPageReq;
import com.mc.payment.core.service.model.req.DictQueryReq;
import com.mc.payment.core.service.model.req.DictSaveReq;
import com.mc.payment.core.service.model.req.DictUpdateReq;
import com.mc.payment.core.service.model.rsp.DictCategoryDescRsp;
import com.mc.payment.core.service.model.rsp.DictRsp;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-23 11:18:13
 */
public interface IDictService extends IService<DictEntity> {
    /**
     * 根据分类编码查询该分类的字典数据
     *
     * @param req
     * @return
     */
    List<DictRsp> dictQuery(DictQueryReq req);

    /**
     * 查询目前支持分类编码
     *
     * @return
     */
    List<DictCategoryDescRsp> dictCategoryCodeQuery();


    /**
     * 分页查询
     *
     * @param req
     * @return
     */
    BasePageRsp<DictEntity> page(DictPageReq req);

    /**
     * 新增数据
     *
     * @param req
     * @return
     */
    RetResult<String> save(DictSaveReq req);

    /**
     * 修改数据
     *
     * @param req
     * @return
     */
    Boolean updateById(DictUpdateReq req);

    /**
     * 查询是否有数据
     *
     * @param req 分类编码+编码
     * @return true->返回是否有数据
     */
    boolean checkExist(DictQueryReq req);

}
