package com.mc.payment.core.service.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.manager.ChannelCostManager;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import com.mc.payment.core.service.model.req.ChannelCostPageReq;
import com.mc.payment.core.service.model.req.ChannelCostSaveReq;
import com.mc.payment.core.service.model.req.ChannelCostUpdateReq;
import com.mc.payment.core.service.model.req.QueryCostAssetListReq;
import com.mc.payment.core.service.model.rsp.ChannelCostPageRsp;
import com.mc.payment.core.service.service.IChannelCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通道成本管理1
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@RequiredArgsConstructor
@Tag(name = "通道成本管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/channel/cost")
public class ChannelCostController extends BaseController {

    private final IChannelCostService channelCostService;

    private final ChannelCostManager channelCostManager;

    @SaCheckPermission("channelCost-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ChannelCostPageRsp>> page(@RequestBody ChannelCostPageReq req) {
        return RetResult.data(channelCostService.page(req));
    }


    @SaCheckPermission("channelCost-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<ChannelCostEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(channelCostManager.getById(id));
    }

    @SaCheckPermission("channelCost-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Valid ChannelCostSaveReq req) {
        return RetResult.data(channelCostManager.save(req));
    }

    @SaCheckPermission("channelCost-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Valid ChannelCostUpdateReq req) {
        return RetResult.data(channelCostManager.updateById(req));
    }

    @SaCheckPermission("channelCost-query")
    @Operation(summary = "获取可选的资产列表", description = "已经配置了成本规则的资产不会再次出现")
    @PostMapping("/queryAssetList")
    public RetResult<List<AssetSimpleDto>> queryAssetList(@RequestBody QueryCostAssetListReq req) {
        return RetResult.data(channelCostManager.queryAssetList(req));
    }

    @Operation(summary = "下载", description = "导出商户可用资产的成本配置数据")
    @GetMapping("/download/{merchantId}")
    public void download(@PathVariable("merchantId") String merchantId) {

    }
}
