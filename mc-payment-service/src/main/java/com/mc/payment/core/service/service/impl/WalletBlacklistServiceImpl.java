package com.mc.payment.core.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.WalletBlacklistEntity;
import com.mc.payment.core.service.mapper.WalletBlacklistMapper;
import com.mc.payment.core.service.service.IWalletBlacklistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.util.MonitorLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 钱包黑名单 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-05-16 15:40:12
 */
@Slf4j
@Service
public class WalletBlacklistServiceImpl extends ServiceImpl<WalletBlacklistMapper, WalletBlacklistEntity> implements IWalletBlacklistService {
    @Autowired
    private AppConfig appConfig;

    @Override
    public boolean isBlacklist(String walletAddress) {
        boolean result = false;
        try {
            long count = this.count(Wrappers.lambdaQuery(WalletBlacklistEntity.class).eq(WalletBlacklistEntity::getWalletAddress, walletAddress));
            if (count > 0) {
                result = true;
                //AML告警指标监控
                JSONObject AMLMonitor = new JSONObject().put("Service","payment").put("MonitorKey","AMLMonitor")
                        .put("Time", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss.SSS"));
                MonitorLogUtil.log(AMLMonitor);
            } else {
                result = getBlacklistLabelCheckUrl(walletAddress);
                if (result) {
                    WalletBlacklistEntity entity = new WalletBlacklistEntity();
                    entity.setWalletAddress(walletAddress);
                    entity.setReason("app.chainaegis.com中获取");
                    this.save(entity);
                    //AML告警指标监控
                    JSONObject AMLMonitor = new JSONObject().put("Service","payment").put("MonitorKey","AMLMonitor")
                            .put("Time", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss.SSS"));
                    MonitorLogUtil.log(AMLMonitor);
                }
            }
        } catch (Exception e) {
            log.error("isBlacklist error", e);
        } finally {
            log.info("isBlacklist walletAddress:{},result:{}", walletAddress, result);
        }
        return result;
    }

    /**
     * 反洗钱校验,判断地址是否在黑名单中
     * <p>
     * https://chainaegiskyt.gitbook.io/cn
     *
     * @param walletAddress
     * @return true 在黑名单
     */
    private boolean getBlacklistLabelCheckUrl(String walletAddress) {
        try {
            String url = appConfig.getChainAegisUrl() + "/kyt/execute";
            Map<String, Object> param = new HashMap<>();
            param.put("appCode", "xsintlg");
            param.put("apiKey", "xsintlg-20240509xxes$1");
            Map<String, Object> businessParam = new HashMap<>();
            businessParam.put("txnId", IdUtil.fastSimpleUUID());
            businessParam.put("txnIP", "192.168.0.1");
            businessParam.put("senderId", "1");
            businessParam.put("senderAddress", walletAddress);
            businessParam.put("receiverId", "1");
            businessParam.put("receiverAddress", walletAddress);
            businessParam.put("txnQuantity", "1");
            businessParam.put("txnTokenPriceUsd", "1");
            businessParam.put("txnTokenName", "1");
            businessParam.put("txnTokenSymbol", "1");
            businessParam.put("network", "1");
            businessParam.put("txnType", "1");

            param.put("businessParam", businessParam);
            String body = HttpUtil.post(url, JSONUtil.toJsonStr(param));

            if (JSONUtil.isTypeJSONObject(body)) {
                // "triggeredResult":"pass" 说明无风险
                JSONObject jsonObject = JSONUtil.parseObj(body);
                if (jsonObject.getInt("code") == 200) {
                    String triggeredResult = jsonObject.getJSONObject("data").getStr("triggeredResult");
                    return !"Pass".equals(triggeredResult);
                }
            }
        } catch (Exception e) {
            log.error("getBlacklistLabelCheckUrl error", e);
        } finally {
            log.info("getBlacklistLabelCheckUrl address:{}", walletAddress);
        }
        return false;
    }
}
