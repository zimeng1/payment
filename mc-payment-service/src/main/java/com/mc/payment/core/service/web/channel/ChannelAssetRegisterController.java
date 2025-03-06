package com.mc.payment.core.service.web.channel;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetRegisterEntity;
import com.mc.payment.core.service.model.req.ChannelAssetRegisterPageReq;
import com.mc.payment.core.service.model.req.ChannelAssetRegisterSaveReq;
import com.mc.payment.core.service.service.IChannelAssetRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Marty
 * @since 2024/6/19 14:43
 */
@Tag(name = "通道资产id管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/register/asset")
public class ChannelAssetRegisterController {

    private final IChannelAssetRegisterService channelAssetRegisterService;

    public ChannelAssetRegisterController(IChannelAssetRegisterService channelAssetRegisterService) {
        this.channelAssetRegisterService = channelAssetRegisterService;
    }

    @SaCheckPermission("registerAsset-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ChannelAssetRegisterEntity>> page(@RequestBody ChannelAssetRegisterPageReq req) {
        return RetResult.data(channelAssetRegisterService.page(req));
    }

    @SaCheckPermission("registerAsset-query")
    @Operation(summary = "列表查询", description = "列表查询")
    @PostMapping("/getList")
    public RetResult<List<ChannelAssetRegisterEntity>> getList() {
        return RetResult.data(channelAssetRegisterService.getList());
    }

    @SaCheckPermission("registerAsset-add")
    @Operation(summary = "注册新币种", description = "注册新币种")
    @PostMapping("/registerAsset")
    public RetResult<String> registerAsset(@RequestBody @Validated ChannelAssetRegisterSaveReq req) {
        return channelAssetRegisterService.registerAsset(req);
    }


}
