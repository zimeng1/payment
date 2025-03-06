package com.mc.payment.fireblocksapi.model.constant;

/**
 * FireBlocks 常量池
 *
 * @author Marty
 * @since 2024/04/11 19:04
 */
public interface FireBlocksConstant {

    /**
     * fireBlocks api 地址
     */
    String BASE_URL = "https://api.fireblocks.io/v1";
//    String BASE_URL = "https://sandbox-api.fireblocks.io/v1";

    /**
     * fireblocks_secret Resource目录下的文件地址
     */
    String SECRET_PATH = "classpath:fireblocks_secret";

    /**
     * 环境变量名 ABASE_URL
     */
    String ENV_FIREBLOCKS_BASE_URL = "FIREBLOCKS_BASE_URL";

    /**
     * 环境变量名 API_KEY
     */
    String ENV_FIREBLOCKS_API_KEY = "FIREBLOCKS_API_KEY";



    /**
     *可以查看工作区。无法执行任何操作
     */
    String VIEWER_ROLE = "VIEWER";

    /**
     * 可以发起交易并提交白名单请求。 ps:全托管
     */
    String EDITOR_ROLE = "EDITOR";

    /**
     * 可以批准白名单地址和交易。无法发起或签署交易。
     */
    String NON_SIGNING_ADMIN_ROLE = "NON_SIGNING_ADMIN";

    /**
     * 可以创建和管理非托管钱包 。 ps:非全托管
     */
    String NCW_ADMIN_ROLE = "NCW_ADMIN";

    /**
     * 可以发起和签署非托管钱包的交易。 ps:非全托管
     */
    String NCW_SIGNER_ROLE = "NCW_SIGNER";

}
