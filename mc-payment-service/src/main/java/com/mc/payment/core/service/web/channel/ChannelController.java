package com.mc.payment.core.service.web.channel;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelEntity;
import com.mc.payment.core.service.facade.ChannelServiceFacade;
import com.mc.payment.core.service.model.req.ChannelPageReq;
import com.mc.payment.core.service.model.req.ChannelSaveReq;
import com.mc.payment.core.service.model.req.ChannelUpdateReq;
import com.mc.payment.core.service.model.rsp.ChannelPageRsp;
import com.mc.payment.core.service.service.IChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道配置管理
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "渠道配置管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/channel")
public class ChannelController extends BaseController {


    @Autowired
    private IChannelService channelService;
    @Autowired
    private ChannelServiceFacade channelServiceFacade;

    @SaCheckPermission("channel-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ChannelPageRsp>> page(@RequestBody ChannelPageReq req) {
        return RetResult.data(channelService.page(req));
    }

    @SaCheckPermission("channel-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<ChannelEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(channelService.getById(id));
    }

    @SaCheckPermission("channel-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated ChannelSaveReq req) {
        return channelServiceFacade.save(req);
    }

    @SaCheckPermission("channel-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated ChannelUpdateReq req) {
        return channelServiceFacade.updateById(req);
    }

//    @Operation(summary = "删除", description = "删除数据")
//    @GetMapping("/removeById/{id}")
//    public RetResult<Boolean> delete(@PathVariable("id") String id) {
//        return channelService.removeById(id);
//    }

    @SaCheckPermission("channel-query")
    @Operation(summary = "子类型列表", description = "查询通道子类型列表")
    @PostMapping("/getChannelNameList")
    public RetResult<List<ChannelEntity>> getChannelNameList() {
        return RetResult.data(channelService.list());
    }

}
