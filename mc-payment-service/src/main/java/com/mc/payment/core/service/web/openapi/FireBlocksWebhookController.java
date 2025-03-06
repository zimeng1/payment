package com.mc.payment.core.service.web.openapi;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.config.aspect.WebhookRecord;
import com.mc.payment.core.service.facade.handler.FireBlocksTypeHandler;
import com.mc.payment.fireblocksapi.util.FireBlocksUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

/**
 * fireBlocks  Webhook 入口
 *
 * @author Marty
 * @since 2024/04/15 19:01
 */
@Tag(name = "fireBlocks Webhook")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/fireBlocks")
public class FireBlocksWebhookController {

    private final Map<String, FireBlocksTypeHandler> fireBlocksTypeHandlerMap;
    private final FireBlocksUtil fireBlocksUtil;

    @WebhookRecord("fireBlocks")
    @Operation(summary = "fireBlocks webhook", description = "fireBlocks webhook event")
    @PostMapping("/webhook")
    public String receiveBlockATMWebhook(@RequestBody String bodyData, HttpServletRequest request) throws Exception {
        log.info("[receiveFireBlocksWebhook] bodyData:{}", bodyData);
        try {
            String signature = request.getHeader("fireblocks-signature");
            if (StrUtil.isBlank(signature)) {
                return "signature validation failed.";
            }
            String publicKey = fireBlocksUtil.getFileContent("classpath:fireblocks_signature_public.key");
            boolean validated = validateSignature(bodyData, signature, publicKey);
            if (!validated) {
                log.error("[receiveFireBlocksWebhook] signature validation failed, bodyData:{}", bodyData);
                return "signature validation failed";
            }

            JSONObject jsonObject = JSONUtil.parseObj(bodyData);
            String type = jsonObject.getStr("type");
            JSONObject data = jsonObject.getJSONObject("data");
            //        String tenantId = data.getString("tenantId");
            FireBlocksTypeHandler<?> fireBlocksTypeHandler = fireBlocksTypeHandlerMap.values().stream()
                    .filter(e -> Arrays.asList(e.type().split(",")).contains(type))
                    .findFirst().orElse(null);
            if (fireBlocksTypeHandler != null) {
                fireBlocksTypeHandler.parseBody(data, type);
            } else {
                log.warn("[receiveFireBlocksWebhook] type handler not exist, type:{}", type);
            }
        } catch (Exception e) {
            log.error("[receiveFireBlocksWebhook] has bean error, bodyData:{}", bodyData, e);
        }
        return "success";
    }

    public static boolean validateSignature(String message, String signature, String publicKey) {
        boolean result = false;
        try {
            publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", ""); // 去除所有空格和换行符
            byte[] derKey = Base64.getDecoder().decode(publicKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(derKey);
            PublicKey pkey = kf.generatePublic(spec);

            // Verify signature
            Signature verifier = Signature.getInstance("SHA512withRSA");
            verifier.initVerify(pkey);
            verifier.update(message.getBytes());

            result =  verifier.verify(Base64.getDecoder().decode(signature));
        } catch (Exception e) {
            log.error("Error in signature validation: {}", e.getMessage());
        }
        return result;
    }
}
