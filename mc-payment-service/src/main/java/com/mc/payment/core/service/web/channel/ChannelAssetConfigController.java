package com.mc.payment.core.service.web.channel;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.manager.ChannelAssetConfigManager;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigPageReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigSaveReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigUpdateReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通道资产配置
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "通道资产")
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/channelAssetConfig")
public class ChannelAssetConfigController extends BaseController {

    private final ChannelAssetConfigService service;
    private final ChannelAssetConfigManager channelAssetConfigManager;

    @SaCheckPermission("channelAsset-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ChannelAssetConfigEntity>> page(@RequestBody ChannelAssetConfigPageReq req) {
        return RetResult.data(service.selectPage(req));
    }

    @SaCheckPermission("channelAsset-query")
    @Operation(summary = "集合查询", description = "下拉列表查询")
    @PostMapping("/list")
    public RetResult<List<ChannelAssetConfigEntity>> list(@RequestBody @Validated ChannelAssetConfigListReq req) {
        return RetResult.data(service.list(req));
    }

    @SaCheckPermission("channelAsset-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<ChannelAssetConfigEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckPermission("channelAsset-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated ChannelAssetConfigSaveReq req) {
        return RetResult.data(channelAssetConfigManager.save(req));
    }

    @SaCheckPermission("channelAsset-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated ChannelAssetConfigUpdateReq req) {
        return RetResult.data(channelAssetConfigManager.updateById(req));
    }

}
