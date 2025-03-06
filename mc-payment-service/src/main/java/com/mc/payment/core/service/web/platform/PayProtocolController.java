package com.mc.payment.core.service.web.platform;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.PayProtocolEntity;
import com.mc.payment.core.service.manager.PayProtocolManager;
import com.mc.payment.core.service.model.req.platform.*;
import com.mc.payment.core.service.service.PayProtocolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "支付协议", description = "法币支付类型/加密货币网络协议")
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/payProtocol")
public class PayProtocolController extends BaseController {

    private final PayProtocolService service;
    private final PayProtocolManager manager;

    @SaCheckPermission("payProtocol-fiat-query")
    @Operation(summary = "法币支付类型-分页查询", description = "分页查询")
    @PostMapping("/fiat/page")
    public RetResult<BasePageRsp<PayProtocolEntity>> page(@RequestBody FiatPayTypePageReq req) {
        return RetResult.data(service.selectFiatPage(req));
    }

    @SaCheckPermission("payProtocol-fiat-query")
    @Operation(summary = "法币支付类型-查询", description = "查询详情")
    @GetMapping("/fiat/getById/{id}")
    public RetResult<PayProtocolEntity> getFiatById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckPermission("payProtocol-fiat-add")
    @Operation(summary = "法币支付类型-新增", description = "新增数据,data:id")
    @PostMapping("/fiat/save")
    public RetResult<String> fiatSave(@RequestBody @Validated FiatPayTypeSaveReq req) {
        return RetResult.data(service.fiatSave(req));
    }

    @SaCheckPermission("payProtocol-fiat-update")
    @Operation(summary = "法币支付类型-修改", description = "修改数据")
    @PostMapping("/fiat/updateById")
    public RetResult<Boolean> fiatUpdateById(@RequestBody @Validated FiatPayTypeUpdateReq req) {
        return RetResult.data(manager.fiatUpdateById(req));
    }

    @SaCheckPermission("payProtocol-crypto-query")
    @Operation(summary = "加密货币网络协议-分页查询", description = "分页查询")
    @PostMapping("/crypto/page")
    public RetResult<BasePageRsp<PayProtocolEntity>> page(@RequestBody CryptoProtocolPageReq req) {
        return RetResult.data(service.selectCryptoPage(req));
    }

    @SaCheckPermission("payProtocol-crypto-query")
    @Operation(summary = "加密货币网络协议-查询", description = "查询详情")
    @GetMapping("/crypto/getById/{id}")
    public RetResult<PayProtocolEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckPermission("payProtocol-crypto-add")
    @Operation(summary = "加密货币网络协议-新增", description = "新增数据,data:id")
    @PostMapping("/crypto/save")
    public RetResult<String> save(@RequestBody @Validated CryptoProtocolSaveReq req) {
        return RetResult.data(service.cryptoSave(req));
    }

    @SaCheckPermission("payProtocol-crypto-update")
    @Operation(summary = "加密货币网络协议-修改", description = "修改数据")
    @PostMapping("/crypto/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated CryptoProtocolUpdateReq req) {
        return RetResult.data(manager.cryptoUpdateById(req));
    }

    @SaCheckPermission({"payProtocol-fiat-query", "payProtocol-crypto-query"})
    @Operation(summary = "下拉列表查询")
    @PostMapping("/list")
    public RetResult<List<PayProtocolEntity>> list(@RequestBody @Validated PayProtocolListReq req) {
        return RetResult.data(service.list(req));
    }
}
