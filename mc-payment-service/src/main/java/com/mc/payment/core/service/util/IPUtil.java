package com.mc.payment.core.service.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Conor
 * @since 2024/6/6 下午3:30
 */
public class IPUtil {

    private IPUtil() {
    }

    final static String IP_REGEX = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";

    public static String getClientIP(HttpServletRequest request) {
        // 尝试从X-Forwarded-For头部获取IP地址
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            // 如果X-Forwarded-For头部不存在或者值为unknown，尝试从X-Real-IP头部获取IP地址
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            // 如果X-Real-IP头部也不存在或者值为unknown，那么就使用request.getRemoteAddr()方法获取IP地址
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.indexOf(",") > 0) {
            // 如果IP地址包含多个（由逗号分隔），取第一个，因为第一个通常是原始客户端的IP地址
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }

    /***
     * 校验多IP地址是否合法, 用','隔开, 匹配格式为: 192.168.1.1,192.168.1.2
     * @param ip
     * @return true: 合法, false: 非法
     */
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return ip.matches(IP_REGEX);
    }


    public static void main(String[] args) {
        //154.17.2.26
        //192.169.3.100
        //8.217.118.128,47.76.147.120
        String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
        String ip = "8.217.118.128,47.76.147.120,192.169.3.100,154.17.2.26";
        if (ip.matches(regex)) {
            System.out.println("ip校验通过");
        }else {
            System.out.println("ip校验不通过");
        }
        String ip1 = "192.168.0.1,199.99.99.张三";
        if (ip1.matches(regex)) {
            System.out.println("ip1校验通过");
        }else {
            System.out.println("ip1校验不通过");
        }
        String ip2 = "192.168.0.1,192.168.1.5,233.18.52";
        if (ip2.matches(regex)) {
            System.out.println("ip2校验通过");
        }else {
            System.out.println("ip2校验不通过");
        }
        String ip3 = "192.168.0,1,192.168.1.5,233.18.52";
        if (ip3.matches(regex)) {
            System.out.println("ip3校验通过");
        }else {
            System.out.println("ip3校验不通过");
        }
    }
}
