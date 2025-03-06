package com.mc.payment.core.service.web.wallet;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletLogEntity;
import com.mc.payment.core.service.model.req.GenerateWalletQRCodeReq;
import com.mc.payment.core.service.model.req.MerchantWalletQueryLogReq;
import com.mc.payment.core.service.model.req.WalletPageReq;
import com.mc.payment.core.service.model.rsp.MerchantWalletRsp;
import com.mc.payment.core.service.service.MerchantWalletLogService;
import com.mc.payment.core.service.service.MerchantWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Conor
 * @since 2024/4/15 下午2:41
 */
@RequiredArgsConstructor
@Tag(name = "商户钱包管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/merchant/wallet")
public class MerchantWalletController {

    private final MerchantWalletService merchantWalletService;
    private final MerchantWalletLogService merchantWalletLogService;


    @SaCheckPermission("wallet-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<MerchantWalletRsp>> page(@RequestBody WalletPageReq req) {
        return RetResult.data(merchantWalletService.page(req));
    }


    @SaCheckPermission("wallet-query")
    @Operation(summary = "生成钱包地址二维码", description = "将参数walletAddress的内容转化为Base64格式的二维码")
    @PostMapping("/generateWalletQRCode")
    public RetResult<String> generateWalletQRCode(@RequestBody @Validated GenerateWalletQRCodeReq req) {
        return RetResult.data(merchantWalletService.generateWalletQRCode(req));
    }

    @SaCheckPermission("wallet-export")
    @Operation(summary = "商户钱包导出", description = "商户钱包导出")
    @GetMapping("/export")
    public void export(WalletPageReq req, HttpServletResponse response) {
        req.setCurrent(1);
        req.setSize(30000);
        merchantWalletService.export(req, response);
    }

    /**
     * 查询商户钱包日志
     *
     * @param req
     * @return
     */
    @SaCheckPermission("merchant-walletLog-query")
    @Operation(summary = "分页查询商户钱包日志", description = "分页查询商户钱包日志")
    @PostMapping("/pageLog")
    public RetResult<BasePageRsp<MerchantWalletLogEntity>> pageLog(@RequestBody MerchantWalletQueryLogReq req) {
        return RetResult.data(merchantWalletLogService.page(req));
    }
}
