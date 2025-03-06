package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.payment.core.service.entity.DictEntity;
import com.mc.payment.core.service.model.rsp.DictCategoryDescRsp;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-04-23 11:18:13
 */
public interface DictMapper extends BaseMapper<DictEntity> {

    @Select("select category_code,category_desc from mcp_dict group by category_code, category_desc")
    List<DictCategoryDescRsp> dictCategoryCodeQuery();

}
