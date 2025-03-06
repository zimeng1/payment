package com.mc.payment.core.service.web;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.model.req.QueryAssetLastQuoteReq;
import com.mc.payment.core.service.service.IAssetLastQuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Marty
 * @since 2024/6/19 14:43
 */
@Tag(name = "资产最新报价管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/assetLastQuote")
public class AssetLastQuoteController {

    private final IAssetLastQuoteService assetLastQuoteService;

    public AssetLastQuoteController(IAssetLastQuoteService assetLastQuoteService) {
        this.assetLastQuoteService = assetLastQuoteService;
    }

    @Operation(summary = "查询", description = "查询详情")
    @PostMapping("/getExchangeRate")
    public RetResult<BigDecimal> getExchangeRate(@RequestBody @Validated QueryAssetLastQuoteReq req) {
        if (AssetConstants.AN_USDT.equals(req.getAssetName())) {
            return RetResult.data(BigDecimal.ONE);
        } else {
            return RetResult.data(assetLastQuoteService.getExchangeRate(req.getAssetName(), true));
        }
    }


}
