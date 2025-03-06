package com.mc.payment.core.service.web.openapi;

import cn.hutool.core.util.StrUtil;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.service.IDepositRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用于重定向请求
 *
 * @author Conor
 * @since 2024-10-12 11:03:58.114
 */
@Tag(name = "重定向请求接口")
@RequiredArgsConstructor
@Controller
@RequestMapping("/redirect")
public class RedirectController {
    private final IDepositRecordService depositRecordService;

    @Operation(summary = "入金成功重定向页面", description = "id为入金记录的id")
    @GetMapping("/deposit/successPage")
    public String depositRedirectPage(String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }
        DepositRecordEntity recordEntity = depositRecordService.getById(id);
        if (recordEntity == null) {
            return null;
        }
        String successPageUrl = recordEntity.getSuccessPageUrl();
        if (StrUtil.isBlank(successPageUrl)) {
            return null;
        }
        return "redirect:" + successPageUrl;
    }

    @Operation(summary = "入金成功重定向页面-post方式", description = "id为入金记录的id")
    @PostMapping("/deposit/post/successPage")
    public String depositPostRedirectPage(String id) {
        return depositRedirectPage(id);
    }
}
