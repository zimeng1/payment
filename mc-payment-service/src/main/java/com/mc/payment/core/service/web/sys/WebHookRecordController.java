package com.mc.payment.core.service.web.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ReceiveWebhookLogEntity;
import com.mc.payment.core.service.model.req.ReceiveWebhookLogReq;
import com.mc.payment.core.service.service.ReceiveWebhookLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebHookRecordController
 *
 * @author GZM
 * @since 2024/10/12 下午6:25
 */
@Slf4j
@Tag(name = "WebHook记录管理")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/webhook/record")
public class WebHookRecordController {

    private final ReceiveWebhookLogService receiveWebhookLogService;

    @SaCheckPermission("receiveWebhookLog-query")
    @Operation(summary = "分页查询日志", description = "分页查询日志")
    @PostMapping("/pageLog")
    public RetResult<BasePageRsp<ReceiveWebhookLogEntity>> page(@RequestBody ReceiveWebhookLogReq req) {
        return RetResult.data(receiveWebhookLogService.page(req));
    }

}