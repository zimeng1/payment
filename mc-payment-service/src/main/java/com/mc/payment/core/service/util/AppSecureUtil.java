package com.mc.payment.core.service.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * 应用安全工具类
 *
 * @author Conor
 * @since 2024-07-25 11:41:15.385
 */
public class AppSecureUtil {

    /**
     * 加密
     * @param text
     * @param key 任意长度的密钥
     * @return
     */
    public static String encrypt(String text, String key) {
        key = StrUtil.padAfter(StrUtil.subWithLength(key, 0, 32), 32, '*');
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes());
        return aes.encryptBase64(text);
    }

    /**
     * 解密
     * @param text
     * @param key 任意长度的密钥
     * @return
     */
    public static String decrypt(String text, String key) {
        key = StrUtil.padAfter(StrUtil.subWithLength(key, 0, 32), 32, '*');
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes());
        return aes.decryptStr(text);
    }

}
