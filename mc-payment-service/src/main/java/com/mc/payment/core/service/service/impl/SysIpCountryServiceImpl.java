package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.SysIpCountryEntity;
import com.mc.payment.core.service.mapper.SysIpCountryMapper;
import com.mc.payment.core.service.service.SysIpCountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Conor
 * @description 针对表【mcp_sys_ip_country(ip归属地记录表)】的数据库操作Service实现
 * @createDate 2024-06-11 17:25:06
 */
@Slf4j
@Service
public class SysIpCountryServiceImpl extends ServiceImpl<SysIpCountryMapper, SysIpCountryEntity>
        implements SysIpCountryService {
    @Override
    public String queryCountry(String ip) {
        // 1.查数据库,有则直接返回
        String countryName = queryByDb(ip);
        if (StrUtil.isNotEmpty(countryName)) {
            return countryName;
        }
        // 2.查询第三方API
        countryName = queryByIpaddressAPI(ip);
        if (StrUtil.isEmpty(countryName)) {
            countryName = queryByIpAPI(ip);
        }
        // 查到保存到数据库中
        if (StrUtil.isNotEmpty(countryName)) {
            SysIpCountryEntity entity = new SysIpCountryEntity();
            entity.setIpAddr(ip);
            entity.setCountryName(countryName);
            this.save(entity);
        }
        return countryName;
    }

    private String queryByDb(String ip) {
        SysIpCountryEntity entity = this.getOne(Wrappers.lambdaQuery(SysIpCountryEntity.class).eq(SysIpCountryEntity::getIpAddr, ip));
        return entity == null ? null : entity.getCountryName();
    }


    private String queryByIpaddressAPI(String ip) {
        String countryName = null;
        try {
            String result = HttpUtil.get("http://api.ipaddress.com/iptocountry?format=json&ip=" + ip);
            log.info("queryByIpaddressAPI ip:{} countryName:{}", ip, result);
            countryName = JSONUtil.parseObj(result).getStr("country_name", "");
        } catch (Exception e) {
            log.error("queryByIpaddressAPI error", e);
        }
        return countryName;
    }

    private String queryByIpAPI(String ip) {
        String countryName = null;
        try {
            String result = HttpUtil.get("http://ip-api.com/json/" + ip);
            log.info("queryByIpAPI ip:{} countryName:{}", ip, result);
            countryName = JSONUtil.parseObj(result).getStr("country", "");
        } catch (Exception e) {
            log.error("queryByIpAPI error", e);
        }
        return countryName;
    }

    /**
     * 获取相邻ip
     *
     * @param ip
     * @return
     */
/*    private List<String> ipList(String ip) {
        List<String> ips = new ArrayList<>();
        String baseIp = ip.substring(0, ip.indexOf('.', ip.indexOf('.') + 1) + 1);
        for (int i = 0; i <= 255; i++) {
            for (int j = 0; j <= 255; j++) {
                String currentIp = baseIp + i + "." + j;
                ips.add(currentIp);
            }
        }
        return ips;
    }*/
}




