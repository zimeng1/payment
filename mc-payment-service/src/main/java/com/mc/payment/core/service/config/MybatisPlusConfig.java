package com.mc.payment.core.service.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.dto.CurrentMerchantDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Objects;

/**
 * @author conor
 * @since 2024/01/26 12:08
 */
@Slf4j
@Configuration
@MapperScan("com.mc.payment.core.service.mapper")
public class MybatisPlusConfig implements MetaObjectHandler {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //interceptor.addInnerInterceptor(new PaginationInnerInterceptor()); 如果有多数据源可以不配具体类型 否则都建议配上具体的DbType
        // 数据权限
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(new MyDataPermissionHandler());
        interceptor.addInnerInterceptor(dataPermissionInterceptor);

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));//如果配置多个插件,切记分页最后添加
        return interceptor;
    }

    //region 添加自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseNoLogicalDeleteEntity baseEntity) {
            // 默认系统自动任务
            Object userName = "System";
            try {
                userName = StpUtil.getExtra("userName");
                log.debug("StpUtil.getExtra userName:{}", userName);
            } catch (Exception e) {
                CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
                if (currentMerchant != null) {
                    // 说明是商户的系统调用了我们的外部接口
                    userName = currentMerchant.getName();
                }
            }
            // 当前登录用户不为空，则创建人和更新人为当前登录用户
            if (Objects.nonNull(userName)) {
                String userNameStr = userName.toString();
                baseEntity.setCreateBy(userNameStr);
                baseEntity.setUpdateBy(userNameStr);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseNoLogicalDeleteEntity baseEntity) {
            // 默认系统自动任务
            Object userName = "System";
            try {
                userName = StpUtil.getExtra("userName");
                log.debug("StpUtil.getExtra userName:{}", userName);
            } catch (Exception e) {
                CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
                if (currentMerchant != null) {
                    // 说明是商户的系统调用了我们的外部接口
                    userName = currentMerchant.getName();
                }
            }

            // 当前登录用户不为空，则当前登录用户为更新人
            if (Objects.nonNull(userName)) {
                baseEntity.setUpdateBy(userName.toString());
            }
            baseEntity.setUpdateTime(new Date());
        }
    }
    //endregion


}
