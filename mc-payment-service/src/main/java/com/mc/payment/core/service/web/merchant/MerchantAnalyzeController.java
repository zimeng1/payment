package com.mc.payment.core.service.web.merchant;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.facade.MerchantAnalyzeServiceFacade;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.AnalyzeSelectionRsp;
import com.mc.payment.core.service.model.rsp.MerchantAnalyzeRsp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户管理
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "商户分析")
@Slf4j
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/merchant")
public class MerchantAnalyzeController extends BaseController {


    @Autowired
    private MerchantAnalyzeServiceFacade merchantAnalyzeServiceFacade;


    @Operation(summary = "商户分析", description = "商户分析")
    @PostMapping("/analyze")
    public RetResult<MerchantAnalyzeRsp> analyze(@RequestBody MerchantQueryReq req) {
        return RetResult.data(merchantAnalyzeServiceFacade.analyze(req));
    }

    @Operation(summary = "五个联动下来数据", description = "商户列表, 账户类型列表, 账户列表, 资产列表, 钱包列表")
    @PostMapping("/analyze/getAnalyzeSelection")
    public RetResult<AnalyzeSelectionRsp> getAnalyzeSelection(@RequestBody MerchantQueryReq req) {
        return RetResult.data(merchantAnalyzeServiceFacade.getAnalyzeSelection(req));
    }

}
