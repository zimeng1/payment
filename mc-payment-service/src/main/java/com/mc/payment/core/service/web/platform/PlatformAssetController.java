package com.mc.payment.core.service.web.platform;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.PlatformAssetEntity;
import com.mc.payment.core.service.manager.PlatformAssetManager;
import com.mc.payment.core.service.model.req.platform.PlatformAssetListReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetPageReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetSaveReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetUpdateReq;
import com.mc.payment.core.service.service.PlatformAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台资产
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "平台资产")
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/platformAsset")
public class PlatformAssetController extends BaseController {

    private final PlatformAssetService service;
    private final PlatformAssetManager manager;

    @SaCheckPermission("platformAsset-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<PlatformAssetEntity>> page(@RequestBody PlatformAssetPageReq req) {
        return RetResult.data(service.selectPage(req));
    }

    @SaCheckPermission("platformAsset-query")
    @Operation(summary = "集合查询", description = "下拉列表查询")
    @PostMapping("/list")
    public RetResult<List<PlatformAssetEntity>> list(@RequestBody @Validated PlatformAssetListReq req) {
        return RetResult.data(service.list(req));
    }


    @SaCheckPermission("platformAsset-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<PlatformAssetEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckPermission("platformAsset-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated PlatformAssetSaveReq req) {
        return RetResult.data(service.save(req));
    }

    @SaCheckPermission("platformAsset-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated PlatformAssetUpdateReq req) {
        return RetResult.data(manager.updateById(req));
    }


}
