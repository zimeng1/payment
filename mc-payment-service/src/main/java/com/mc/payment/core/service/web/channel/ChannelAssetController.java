package com.mc.payment.core.service.web.channel;

import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通道资产
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "通道资产")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/channel/asset")
public class ChannelAssetController extends BaseController {
/*

    @Autowired
    private IChannelAssetService channelAssetService;

    @SaCheckPermission("channelAsset-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ChannelAssetEntity>> page(@RequestBody ChannelAssetPageReq req) {
        return RetResult.data(channelAssetService.page(req));
    }

    @SaCheckPermission("channelAsset-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<ChannelAssetEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(channelAssetService.getById(id));
    }

    @SaCheckPermission("channelAsset-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated ChannelAssetSaveReq req) {
        return channelAssetService.save(req);
    }


    @SaCheckPermission("channelAsset-remove")
    @Operation(summary = "删除", description = "删除数据")
    @GetMapping("/removeById/{id}")
    public RetResult<Boolean> delete(@PathVariable("id") String id) {
        return channelAssetService.removeById(id);
    }


    //查询数据列表
    @Operation(summary = "查询通道资产名称列表", description = "分页查询时, 获取通道资产名称列表数据")
    @PostMapping("/getChannelAssetNameList")
    public RetResult<List<ChannelAssetEntity>> getChannelAssetNameList(@RequestBody ChannelAssetPageReq req) {
        return RetResult.data(channelAssetService.getChannelAssetNameList(req));
    }

    @Operation(summary = "查询fireBlocks支持的资产列表", description = "新增/修改时, 获取通道资产名称列表数据")
    @GetMapping("/getFireBlocksSupportedAssetList")
    public RetResult<List<ChannelAssetEntity>> getFireBlocksSupportedAssetList() {
        return RetResult.data(channelAssetService.getFireBlocksSupportedAssetList());
    }*/
}
