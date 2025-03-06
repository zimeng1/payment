package com.mc.payment.core.service.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import java.lang.reflect.Method;

/**
 * 数据权限
 * 注意:mybatis plus 有bug 默认拦截了所有的sql，所以需要自定义注解标记需要处理的sql MerchantFilter
 * bug为:
 * InterceptorIgnore 里的 dataPermission 默认为1 ，是想表达默认情况下是不启用数据权限的。
 * 但是InterceptorIgnoreHelper里willIgnoreDataPermission调用的willIgnore，导致如果不加注解，默认是启用数据权限的，这里就与前面的InterceptorIgnore 和大部分业务场景相违背了。这里应该是默认没注解则返回true忽略掉。否则不可能把其他大部分不启用数据权限的mapper加上注解来忽略撒。
 * 作者未对其进行修复,为了避免在每一个mapper上加上忽略注解,所有增加了一个自定义注解 @MerchantFilter
 * <p>
 * 需要进行数据权限过滤的sql需要在mapper方法上加上 @MerchantFilter 注解  当前只支持对商户的过滤
 *
 * @author Conor
 * @since 2024-06-07 11:41:15.696
 */
@Slf4j
public class MyDataPermissionHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {

        MerchantFilter merchantFilter = getMerchantFilter(mappedStatementId);
        if (merchantFilter != null && TokenUtil.isLogin()) {
            log.info("数据权限处理器 getSqlSegment where: {}, mappedStatementId: {}", where, mappedStatementId);

            // 添加商户ID的过滤条件
            String field = merchantFilter.value();
            Object object = StpUtil.getExtra("userMerchantIds");
            if (object == null) {
                // 登录了 但是没userMerchantIds 是测试人员登录了后端管理系统 携带了那边的登录信息
                return where;
            }
            String userMerchantIds = object.toString();
            if ("*".equals(userMerchantIds)) {
                return where;
            }
            // 用户没有关联商户的话,不返回任何数据
            if (userMerchantIds == null || userMerchantIds.isEmpty()) {
                userMerchantIds = "''";
            }
            // 添加过滤条件，例如：field = #{merchantId}
            StringBuilder inClause = new StringBuilder(field + " IN (").append(userMerchantIds).append(")");

            Expression inExpression = null;
            try {
                inExpression = CCJSqlParserUtil.parseCondExpression(inClause.toString());
            } catch (JSQLParserException e) {
                log.error("解析商户ID过滤条件失败", e);
            }

            if(where == null && inExpression != null){
                return inExpression;
            }
            // 如果原始的WHERE条件不为空，把新的等式条件和原始的WHERE条件组合起来
            if (inExpression != null) {
                return new AndExpression(where, inExpression);
            }

        }
        return where;
    }

    private MerchantFilter getMerchantFilter(String mappedStatementId) {
        try {
            int lastDotIndex = mappedStatementId.lastIndexOf(".");
            String className = mappedStatementId.substring(0, lastDotIndex);
            String methodName = mappedStatementId.substring(lastDotIndex + 1);
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(MerchantFilter.class);
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", mappedStatementId, e);
        }
        return null;
    }

}
