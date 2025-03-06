package com.mc.payment.core.service.service;

import com.mc.payment.core.service.entity.SysIpCountryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Conor
* @description 针对表【mcp_sys_ip_country(ip归属地记录表)】的数据库操作Service
* @createDate 2024-06-11 17:25:06
*/
public interface SysIpCountryService extends IService<SysIpCountryEntity> {

    String queryCountry(String ip);
}
