package com.mc.payment.core.service.web.sys;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.DictEntity;
import com.mc.payment.core.service.model.req.DictPageReq;
import com.mc.payment.core.service.model.req.DictQueryReq;
import com.mc.payment.core.service.model.req.DictSaveReq;
import com.mc.payment.core.service.model.req.DictUpdateReq;
import com.mc.payment.core.service.model.rsp.DictCategoryDescRsp;
import com.mc.payment.core.service.model.rsp.DictRsp;
import com.mc.payment.core.service.service.IDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Conor
 * @since 2024/4/23 上午11:18
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/dict")
public class DictController extends BaseController {

    @Autowired
    private IDictService dictService;

    @Operation(summary = "根据分类编码查询", description = "查询")
    @PostMapping("/dictQuery")
    public RetResult<List<DictRsp>> dictQuery(@Valid @RequestBody DictQueryReq req) {
        return RetResult.data(dictService.dictQuery(req));
    }

    @Operation(summary = "查询分类编码和信息", description = "分页查询")
    @GetMapping("/dictCategoryCodeQuery")
    public RetResult<List<DictCategoryDescRsp>> dictCategoryCodeQuery() {
        return RetResult.data(dictService.dictCategoryCodeQuery());
    }


    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<DictEntity>> page(@RequestBody DictPageReq req) {
        return RetResult.data(dictService.page(req));
    }

    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<DictEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(dictService.getById(id));
    }

    @Operation(summary = "新增", description = "新增数据")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Valid DictSaveReq req) {
        return dictService.save(req);
    }

    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Valid DictUpdateReq req) {
        return RetResult.data(dictService.updateById(req));
    }

    @Operation(summary = "删除", description = "删除数据")
    @GetMapping("/removeById/{id}")
    public RetResult<Boolean> delete(@PathVariable("id") String id) {
        return RetResult.data(dictService.removeById(id));
    }

}
