package com.mc.payment.core.service.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app")
@EnableConfigurationProperties(AppConfig.class)
public class AppConfig implements InitializingBean, DisposableBean {

    private String payoutGatewayAddress;

    private int chainId;

    // 入金失效时长
    private int depositExpire = 86400000;
    // 默认入金账户数量
    private int defaultDepositAccountQuantity = 10;
    // 默认出金账户数量
    private int defaultWithdrawalAccountQuantity = 3;

    // 是否开启AKSK校验 1:是,0:否
    private int akSkEnabled = 1;
    // 是否开启外部API IP校验 1:是,0:否
    private int externalAPIIPEnabled = 1;

    // mt5服务的url地址
    private String mt5ApiUrl;

    // 新增账号个数
    private int createAccountCount = 1;


    // 预估费接口临时使用: 账号ExternalId
    private String accountExternalId;
    // 预估费接口临时使用: 账号名
    private String accountName;
    private String destinationAddress;

    // ChainAegis URL地址
    private String chainAegisUrl = "https://app.chainaegis.com/gateway";
    private String chainAegisAppCode = "xsintlg";
    private String chainAegisApiKey = "xsintlg-20240509xxes$1";

    // 币安  现货/杠杆/币安宝/矿池 接口地址
    private String binanceSpotUrl = "https://api.binance.com";

    // 产研团队内部告警接收邮箱,英文逗号隔开
    private String alertReceiveEmail;

    // 是否开启出金地址校验 1:是,0:否
    private int withdrawalAddressEnabled = 1;

    // 本服务前端域名
    private String paymentDomain;

    //本服务后端域名 https://test-gateway.mcconnects.com/mc-payment
    private String paymentRealend;

    // 收银页参数的加密密钥
    private String cashierKey = "7x*&J%k97I@Con";

    // exchangerate-api key  14cc3ad6b65ebbc5332f15d5
    // 测试 c272c02e3283ca4b22ab2b87 f0e6e91dd0655ab45b518bfd
    private String exchangeRateApiKey = "f0e6e91dd0655ab45b518bfd";
    // 钱包冷却时长 默认为24小时
    private Long walletCooldownTime = 24 * 60 * 60 * 1000L;
    // 测试环境使用的商户ID,用来屏蔽垃圾数据
    private String testSyncMerchantIds;
    // 钱包最低可用数
    private int minWalletAvailableNum = 1;
    // 生成钱包数量
    private int createWalletCount = 5;
    // paypal approved webhook id
    private String paypalApprovedWebhookId;

    // PayPal payoutsItem succeeded webhook id
    private String paypalPayoutsItemWebhookId;

    // 临时使用:开发接口-资产列表按商户过滤
    private String merchantEncryptAssetMap;
    private String merchantFiatAssetMap;
    // 临时使用:需要屏蔽的PayPal商户ID
    private String ignorePayPalMerchantIds;

    private String passToPaySecretKey;
    private String passToPayAppId;
    private String passToPayMchNo;

    //cheezeePay相关配置
    private String cheezeePayMchNo;

    private String cheezeePayAppId;

    // crm 服务token
    private String crmToken = "577235a75c8e44ad8706f2f23ab3975e";


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("[afterPropertiesSet()] {}", this);
    }

    @Override
    public void destroy() throws Exception {
        log.info("[destroy()] {}", this);
    }

}
