package com.mc.payment.core.service.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.mc.payment.core.service.config.interceptor.AKSKInterceptor;
import com.mc.payment.core.service.config.interceptor.IPInterceptor;
import com.mc.payment.core.service.config.interceptor.TraceIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Lazy
    @Autowired
    private AKSKInterceptor akskInterceptor;
    @Lazy
    @Autowired
    private TraceIdInterceptor traceIdInterceptor;
    @Autowired
    private IPInterceptor ipInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceIdInterceptor).addPathPatterns("/**");
        registry.addInterceptor(akskInterceptor).addPathPatterns("/external/**", "/openapi/**")
                .excludePathPatterns("/openapi/webhook/**");  // 这里指定了只拦截 /external 路径下的请求
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> {
                    SaRouter.match("/api/**").check(r -> StpUtil.checkLogin());
                }).isAnnotation(true))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/auth/**",
                        "/api/v1/fireBlocks/**",
                        "/api/v1/test/webhook/test",
                        "/api/v1/ip/**",
                        "/api/v1/payment/page/**",
                        "/redirect/**");
        // ip白名单拦截
//        registry.addInterceptor(ipInterceptor).addPathPatterns("/api/**")
//                .excludePathPatterns("/api/v1/auth/**", "/api/v1/fireBlocks/**", "/external/**", "/api/v1/ip/**");
        // 注意: ,"/api/v1/ip/**" 这个路径是不需要拦截的 查询ip归属地的接口 该功能临时放到payment项目中
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//    }

}
