package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.EmailTemplateEntity;
import com.mc.payment.core.service.entity.JobPlanEntity;
import com.mc.payment.core.service.model.dto.EmailJobParamDto;
import com.mc.payment.core.service.model.enums.EmailContentEnum;
import com.mc.payment.core.service.model.enums.JobPlanHandlerEnum;
import com.mc.payment.core.service.service.IEmailTemplateService;
import com.mc.payment.core.service.service.IJobPlanService;
import com.mc.payment.core.service.service.IWithdrawalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author Marty
 * @since 2024/6/11 15:42
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WithdrawalRecordServiceFacade {

    private final ExternalServiceFacade externalServiceFacade;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final AppConfig appConfig;
    private final IJobPlanService jobPlanService;
    private final IEmailTemplateService emailTemplateService;
    public static final String ALARM_EMAIL_SUBJECT = "MCPayment系统警告：检测到异常出金目标地址活动";


    // scanInsufficientBalanceJob接口创建
    public void scanInsufficientBalanceJob() {
//        try {
//            log.info("[scanInsufficientBalanceJob]扫描余额不足的出金申请, time: {}", new Date());
//            // 获取余额不足的出金申请
//            List<WithdrawalRecordEntity> list = externalServiceFacade.getInsufficientBalanceList();
//            if (CollUtil.isEmpty(list)) {
//                log.info("[scanInsufficientBalanceJob]未扫描到余额不足的出金申请");
//                return;
//            }
//
//            //处理List<WithdrawalRecordEntity> list集合的数据, 将创建时间超过15天的数据和不超过2小时的数据分开放到list
//            List<WithdrawalRecordEntity> listOver = new ArrayList<>();
//            List<WithdrawalRecordEntity> listLess = new ArrayList<>();
//            for (WithdrawalRecordEntity entity : list) {
//                if (entity.getCreateTime().before(DateUtil.offsetHour(new Date(), -2))) {
//                    entity.setStatus(WithdrawalRecordStatusEnum.ITEM_6.getCode());
//                    listOver.add(entity);
//                } else {
//                    listLess.add(entity);
//                }
//            }
//            //如果有超过2小时的数据,则将这些数据的状态改为6. 超过2小时余额不足, 那这数据就不再处理了, 让商户自行发起新的出金申请,
//            // ps: 因为是每次都会处理, 所以这里批量修改状态可以不考虑事务
//            externalServiceFacade.withdrawalUpdateBatchById(listOver);
//
//            //如果有未15天的数据,则重新走一遍刷新余额流程
//            if (CollUtil.isNotEmpty(listLess)) {
//                //循环调度刷新余额接口
//                for (WithdrawalRecordEntity entity : listLess) {
//                    try {
//                        externalServiceFacade.withdrawalRecordRefresh(entity.getId());
//                    } catch (Exception e) {
//                        log.error("[scanInsufficientBalanceJob]扫描余额不足的出金申请, 并重新刷新余额, 异常, id:{}", entity.getId(), e);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("[scanInsufficientBalanceJob]扫描余额不足的出金申请, 并进行相关业务处理,异常", e);
//        }
    }

    /**
     * 出金风控扫描,以及触发邮件预警
     */
    public void riskScan() {
        List<String> addressesOver1500U = withdrawalRecordService.listOverAmountAddress(24, 1500);
        List<String> addressesOver5000U = withdrawalRecordService.listOverAmountAddress(24 * 7, 5000);
        List<String> addressesMoreThan5Times = withdrawalRecordService.listOverTimesAddress(24, 5);

        if (CollUtil.isEmpty(addressesOver1500U) && CollUtil.isEmpty(addressesOver5000U) && CollUtil.isEmpty(addressesMoreThan5Times)) {
            return;
        }
        String alertReceiveEmail = appConfig.getAlertReceiveEmail();
        if (StrUtil.isEmpty(alertReceiveEmail)) {
            log.info("未配置告警邮箱,无法发送告警邮件");
            return;
        }
        String[] emails = alertReceiveEmail.split(",");

        List<String> addressesOver1500URemove = new ArrayList<>();
        List<String> addressesOver5000URemove = new ArrayList<>();
        List<String> addressesMoreThan5TimesRemove = new ArrayList<>();
        for (String email : emails) {
            for (String address : addressesOver1500U) {
                boolean checkSendEmail = this.checkSendEmail(email, address);
                if (checkSendEmail) {
                    addressesOver1500URemove.add(address);
                }
            }

            for (String address : addressesOver5000U) {
                boolean checkSendEmail = this.checkSendEmail(email, address);
                if (checkSendEmail) {
                    addressesOver5000URemove.add(address);
                }
            }
            for (String address : addressesMoreThan5Times) {
                boolean checkSendEmail = this.checkSendEmail(email, address);
                if (checkSendEmail) {
                    addressesMoreThan5TimesRemove.add(address);
                }
            }

        }
        addressesOver1500U = new ArrayList<>(addressesOver1500U);
        addressesOver5000U = new ArrayList<>(addressesOver5000U);
        addressesMoreThan5Times = new ArrayList<>(addressesMoreThan5Times);
        addressesOver1500U.removeAll(addressesOver1500URemove);
        addressesOver5000U.removeAll(addressesOver5000URemove);
        addressesMoreThan5Times.removeAll(addressesMoreThan5TimesRemove);
        if (CollUtil.isEmpty(addressesOver1500U) && CollUtil.isEmpty(addressesOver5000U) && CollUtil.isEmpty(addressesMoreThan5Times)) {
            return;
        }

        //告警
        try {
            List<String> emailContent = generateEmailContentList(addressesOver1500U, addressesOver5000U, addressesMoreThan5Times);
            for (String email : emails) {
                for (int i = 0; i < emailContent.size(); i++) {
                    EmailJobParamDto emailJobParamDto = new EmailJobParamDto(email,
                            ALARM_EMAIL_SUBJECT,
                            emailContent.get(i));
                    jobPlanService.addJobPlan(JobPlanHandlerEnum.SEND_EMAIL, emailJobParamDto);
                }
            }
        } catch (Exception e) {
            log.error("出金告警失败", e);
        }
    }

    /**
     * 检查是否已经发送过邮件
     *
     * @return
     */
    public boolean checkSendEmail(String recipientMail, String address) {
        return jobPlanService.count(Wrappers.lambdaQuery(JobPlanEntity.class)
                .eq(JobPlanEntity::getJobHandler, JobPlanHandlerEnum.SEND_EMAIL.getJobHandler())
                .ge(BaseNoLogicalDeleteEntity::getCreateTime, DateUtil.offsetDay(new Date(), -1))
                .apply("JSON_EXTRACT(param, '$.subject') = {0}", ALARM_EMAIL_SUBJECT)
                .apply("JSON_EXTRACT(param, '$.recipientMail') = {0}", recipientMail)
                .apply("JSON_UNQUOTE(JSON_EXTRACT(param, '$.content')) LIKE CONCAT('%',{0},'%')", address)
        ) > 0;
    }

    public String generateEmailContent(List<String> addressesOver1500U, List<String> addressesOver5000U, List<String> addressesMoreThan5Times) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("系统监控警告，\n\n");
        emailContent.append("MCPayment系统监控已检测到一些特定的出金目标地址显示出了异常活动。以下是详细信息：\n\n");

        if (!addressesOver1500U.isEmpty()) {
            emailContent.append("· 在过去的24小时内，以下地址的提币金额超过了1500 U：\n");
            for (String address : addressesOver1500U) {
                emailContent.append("   - " + address + "\n");
            }
            emailContent.append("\n");
        }

        if (!addressesOver5000U.isEmpty()) {
            emailContent.append("· 在过去的7天内，以下地址的提币金额超过了5000 U：\n");
            for (String address : addressesOver5000U) {
                emailContent.append("   - " + address + "\n");
            }
            emailContent.append("\n");
        }

        if (!addressesMoreThan5Times.isEmpty()) {
            emailContent.append("· 在过去的24小时内，以下地址的提币次数超过了5次：\n");
            for (String address : addressesMoreThan5Times) {
                emailContent.append("   - " + address + "\n");
            }
            emailContent.append("\n");
        }

        emailContent.append("这些活动可能表明存在风险，需要立即进行调查。请查看附件中的详细报告，其中包含了所有被标记为高风险的钱包地址。\n\n");
        emailContent.append("我们强烈建议您立即查看这些账户的活动，并采取必要的行动以保护我们的系统和用户。如果您需要任何进一步的帮助或信息，请随时联系我们。\n\n");
        emailContent.append("保持警惕，确保我们的系统安全是我们的首要任务。感谢您的理解和合作。\n\n");
        emailContent.append("系统监控团队");

        return emailContent.toString();
    }

    public List<String> generateEmailContentList(List<String> addressesOver1500U, List<String> addressesOver5000U, List<String> addressesMoreThan5Times) {
        List<String> result = new ArrayList<>();

        StringBuilder emailContent = null;

        Map<String, EmailTemplateEntity> emailTemplateCacheMap = emailTemplateService.getEmailTemplateCacheMap();
        EmailTemplateEntity emailTemplateEntity = emailTemplateCacheMap.get(EmailContentEnum.RISK_ALARM.getCode());
        String content = emailTemplateEntity.getContent();

        if (!addressesOver1500U.isEmpty()) {
            emailContent = new StringBuilder();
            emailContent.append("<br/>");
            for (String address : addressesOver1500U) {
                emailContent.append("    " + address + "<br/>");
            }

            emailContent.append(content.replaceFirst("%s", "钱包地址")
                    .replaceFirst("%s", emailContent.toString())
                    .replaceFirst("%s", "在过去的24小时内，以下地址的提币金额超过了1500"));
            result.add(emailContent.toString());
        }

        if (!addressesOver5000U.isEmpty()) {
            emailContent = new StringBuilder();
            emailContent.append("<br/>");
            for (String address : addressesOver5000U) {
                emailContent.append("    " + address + "<br/>");
            }

            emailContent.append(content.replaceFirst("%s", "钱包地址")
                    .replaceFirst("%s", emailContent.toString())
                    .replaceFirst("%s", "在过去的7天内，以下地址的提币金额超过了5000"));
            result.add(emailContent.toString());
        }

        if (!addressesMoreThan5Times.isEmpty()) {
            emailContent = new StringBuilder();
            emailContent.append("<br/>");
            for (String address : addressesMoreThan5Times) {
                emailContent.append("    " + address + "<br/>");
            }

            emailContent.append(content.replaceFirst("%s", "钱包地址")
                    .replaceFirst("%s", emailContent.toString())
                    .replaceFirst("%s", "在过去的24小时内，以下地址的提币次数超过了5次"));
            result.add(emailContent.toString());
        }

        return result;
    }


    public void payoutTimeoutLimit(){
        withdrawalRecordService.payoutTimeoutLimit();
    }
}
