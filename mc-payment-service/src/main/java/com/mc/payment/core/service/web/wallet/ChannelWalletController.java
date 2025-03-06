package com.mc.payment.core.service.web.wallet;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelWalletEntity;
import com.mc.payment.core.service.entity.ChannelWalletLogEntity;
import com.mc.payment.core.service.model.req.ChannelWalletPageReq;
import com.mc.payment.core.service.model.req.ChannelWalletQueryLogPageReq;
import com.mc.payment.core.service.service.ChannelWalletLogService;
import com.mc.payment.core.service.service.ChannelWalletService;
import com.mc.payment.core.service.service.IWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Conor
 * @since 2024/4/15 下午2:41
 */
@Slf4j
@Tag(name = "通道钱包管理")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/channel/wallet")
public class ChannelWalletController {

    private final IWalletService walletService;
    private final ChannelWalletService channelWalletService;
    private final ChannelWalletLogService channelWalletLogService;

//    @Operation(summary = "迁移旧钱包数据到商户钱包和通道钱包", description = "上线做数据迁移时使用")
//    @GetMapping("/dataMigrate")
//    public RetResult dataMigrate() {
//        walletService.dataMigrate();
//        return RetResult.ok();
//    }

    @SaCheckPermission("channel-wallet-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ChannelWalletEntity>> page(@RequestBody ChannelWalletPageReq req) {
        return RetResult.data(channelWalletService.page(req));
    }

    @SaCheckPermission("channel-wallet-export")
    @Operation(summary = "通道钱包导出", description = "通道钱包导出")
    @GetMapping("/export")
    public void export(ChannelWalletPageReq req, HttpServletResponse response) {
        req.setCurrent(1);
        req.setSize(30000);
        channelWalletService.export(req, response);
    }

    @SaCheckPermission("channel-walletLog-query")
    @Operation(summary = "分页查询通道钱包日志", description = "分页查询通道钱包日志")
    @PostMapping("/pageLog")
    public RetResult<BasePageRsp<ChannelWalletLogEntity>> pageLog(@RequestBody ChannelWalletQueryLogPageReq req) {
        return RetResult.data(channelWalletLogService.page(req));
    }
}
