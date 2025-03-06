package com.mc.payment.core.service.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.mc.payment.api.util.AKSKUtil;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.config.RequestContext;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.dto.CurrentMerchantDto;
import com.mc.payment.core.service.service.IMerchantService;
import com.mc.payment.core.service.util.IPUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class AKSKInterceptor implements HandlerInterceptor {
    // 用来获取商户 SK 的服务
    private final IMerchantService merchantService;
    private final AppConfig appConfig;

    public AKSKInterceptor(IMerchantService merchantService, AppConfig appConfig) {
        this.merchantService = merchantService;
        this.appConfig = appConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String ak = request.getHeader("X-Access-Key");
            String signature = request.getHeader("X-Signature");
            String requestTimestamp = request.getHeader("X-Timestamp");
            String xRequestURI = request.getHeader("X-RequestURI");
            String ip = IPUtil.getClientIP(request);
            log.info("IP:{},X-Access-Key: {}, X-Signature: {}, X-Timestamp: {},X-RequestURI:{}", ip, ak, signature, requestTimestamp, xRequestURI);
            if (ak == null || signature == null || requestTimestamp == null) {
                log.error("ak or signature or requestTimestamp is null");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized: X-Access-Key or X-Signature or X-Timestamp is null.");
                response.getWriter().flush();
                return false;
            }

            String requestURI = request.getRequestURI();
            // 如果传了 则直接比较是否和当前uri是否相等
            if (StrUtil.isNotBlank(xRequestURI) && !requestURI.equals(xRequestURI)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().println("unauthorized: The requestURI does not match the one required by the current API.");
                response.getWriter().flush();
                return false;
            }

            if (appConfig.getAkSkEnabled() == 0) {
                log.info("ak sk disabled");
                MerchantEntity merchantEntity = merchantService.getByAK(ak);
                if (merchantEntity == null) {
                    log.error("Merchant not found for ak: {}", ak);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().println("Unauthorized: Merchant not found for ak:" + ak);
                    response.getWriter().flush();
                    return false;
                }
                RequestContext.setCurrentMerchant(CurrentMerchantDto.valueOf(merchantEntity));
                return true;
            }
            if (isRequestExpired(requestTimestamp)) {
                log.error("requestExpired ak: {},requestTimestamp:{}", ak, requestTimestamp);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("unauthorized: the request is no longer within the time window(5 minutes).");
                response.getWriter().flush();
                return false;
            }
            // 获取 SK
            MerchantEntity merchantEntity = merchantService.getByAK(ak);
            if (merchantEntity == null) {
                log.error("Merchant not found for ak: {}", ak);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized: Merchant not found for ak:" + ak);
                response.getWriter().flush();
                return false;
            }

            if (appConfig.getExternalAPIIPEnabled() == 1) {
                // ip白名单校验 ipWhitelist格式为用英文逗号隔开的ip地址
                String ipWhitelist = merchantEntity.getIpWhitelist();
                if (StrUtil.isBlank(ipWhitelist) || !ipWhitelist.contains(ip)) {
                    log.error("ip:{} not in whitelist:{}", ip, ipWhitelist);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Unauthorized: IP is not in whitelist.");
                    response.getWriter().flush();
                    return false;
                }
            }

            String sk = merchantEntity.getSecretKey();

            RequestContext.setCurrentMerchant(CurrentMerchantDto.valueOf(merchantEntity));

            // 验证签名
            boolean isValid = validateSignature(ak, requestTimestamp, requestURI, signature, sk);

            log.info("requestURI:{},isValid:{}", requestURI, isValid);
            if (isValid) {
                return true;
            }
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized: The signature is API. " +
                    "Please verify that your SecretKey and RequestURI match those required by the current API." +
                    "The RequestURI needed for the current API is:" + requestURI);
            response.getWriter().flush();
            return false;
        } catch (Exception e) {
            log.error("Error handling request: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Internal Server Error");
            response.getWriter().flush();
            return false;
        }
    }

    public boolean validateSignature(String accessKey, String requestTimestamp, String requestURI, String signature, String secretKey) {
        // 1. 使用相同的方式重新生成签名
        String calculatedSignature = AKSKUtil.calculateHMAC(accessKey + requestTimestamp + requestURI, secretKey);

        // 2. 比较重新生成的签名和请求中的签名
        return calculatedSignature.equals(signature);
    }

    public boolean isRequestExpired(String requestTimestamp) {
        long currentTime = System.currentTimeMillis();
        long timeWindow = 5 * 60 * 1000;  // 5 minutes

        // Check if the request is within the time window
        // The request is not expired
        return Math.abs(currentTime - Long.valueOf(requestTimestamp)) > timeWindow;  // The request is expired
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestContext.clear();
    }
}
