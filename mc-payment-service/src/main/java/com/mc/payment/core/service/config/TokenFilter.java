package com.mc.payment.core.service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 自定义Filter
 * 对请求的header 过滤token
 *
 * 过滤器Filter可以拿到原始的HTTP请求和响应的信息，
 *     但是拿不到你真正处理请求方法的信息，也就是方法的信息
 *
 * @Component 注解让拦截器注入Bean，从而让拦截器生效
 * @WebFilter 配置拦截规则
 *
 * 拦截顺序：filter—>Interceptor-->ControllerAdvice-->@Aspect -->Controller
 *
 */
@Slf4j
//@Component
//@WebFilter(urlPatterns = {"/**"},filterName = "tokenAuthorFilter")
public class TokenFilter implements Filter {
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TokenFilter init {}",filterConfig.getFilterName());
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("TokenFilter doFilter 我拦截到了请求:{}",((HttpServletRequest)request).getRequestURI());
//        log.info("TokenFilter doFilter",((HttpServletRequest)request).getHeader("token"));
 
        chain.doFilter(request,response);//到下一个链
 
    }
 
    @Override
    public void destroy() {
        log.info("TokenFilter destroy");
    }
}