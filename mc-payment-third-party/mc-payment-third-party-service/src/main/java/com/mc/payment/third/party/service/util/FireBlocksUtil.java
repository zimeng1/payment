package com.mc.payment.third.party.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fireblocks.sdk.ConfigurationOptions;
import com.fireblocks.sdk.Fireblocks;
import com.fireblocks.sdk.InstanceTimeWrapper;
import com.mc.payment.third.party.api.model.constant.FireBlocksConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Flow;

/**
 * @author Marty
 * @since 2024/04/13 11:18
 */
@Slf4j
@Component
public class FireBlocksUtil {

    private volatile static Fireblocks fireblocks;

    @Resource
    private ResourceLoader resourceLoader;

    @Value("${app.fire.blocks.apiKey}")
    private String apiKey;

    @Value("${app.fire.blocks.baseUrl}")
    private String baseUrl;

    @Value("${app.fire.blocks}")
    private String env;

    public ConfigurationOptions getConfigurationOptions() {
        // url和key 优先取环境变量, 没有再走配置中心,配置文件. secretKey就只走读取文件.(2024,04,20)
        String envBaseUrl = System.getenv(FireBlocksConstant.ENV_FIREBLOCKS_BASE_URL);
        String url = StringUtils.isBlank(envBaseUrl) ? baseUrl : envBaseUrl;
        String envApiKey = System.getenv(FireBlocksConstant.ENV_FIREBLOCKS_API_KEY);
        String key = StringUtils.isBlank(envApiKey) ? apiKey : envApiKey;
        String secretKey;
        if (StringUtils.isNotBlank(env)) {
            secretKey = getFileContent(FireBlocksConstant.SECRET_PATH + "_" + env + ".key");
        } else {
            secretKey = getFileContent(FireBlocksConstant.SECRET_PATH + ".key");
        }

/*        //本地测试环境
        String url = "https://api.fireblocks.io/v1";
        String key = "696ad22d-7d34-421e-b084-9e501bdd6187";
        String secretKey = getFileContent("classpath:fireblocks_secret_test.key");
        // 沙盒环境
        String url = "https://sandbox-api.fireblocks.io/v1";
        String key = "028e9910-428a-4364-8dac-dde86ea0c59b";
        String secretKey = getFileContent("classpath:fireblocks_secret_dev.key");*/

        return new ConfigurationOptions().basePath(url).apiKey(key).secretKey(secretKey);
    }

    public Fireblocks getFireBlocks() {
        if (fireblocks == null) {
            synchronized (FireBlocksUtil.class) {
                if (fireblocks == null) {
                    fireblocks = new Fireblocks(getConfigurationOptions());
                }
            }
        }
        return fireblocks;
    }

    public String getFileContent(String resPathName) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try {
            org.springframework.core.io.Resource resource = resourceLoader.getResource(resPathName);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
//                resultStringBuilder.append(line).append("\n");
                resultStringBuilder.append(line);
            }
        } catch (Exception e) {
            log.error("[getFileContent]  has bean error, 获取文件内容失败, resPathName:{}, 异常为:", resPathName, e);
        }
        return resultStringBuilder.toString();
    }


    public String doFireBlocksApiByUrl(String url, String method) throws Exception {
//        String assetId = "$ASR_$CHZ";
//        String url = "https://api.fireblocks.io/v1/vault/accounts/30541/" + assetId;
//        https://api.fireblocks.io/v1/vault/accounts/{vaultAccountId}/{assetId}
//        String sk = "-----BEGIN PRIVATE KEY-----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-----END PRIVATE KEY-----";
//        String ak = "696ad22d-7d34-421e-b084-9e501bdd6187";
        ConfigurationOptions configurationOptions = getConfigurationOptions();
        String secretKey = configurationOptions.getSecretKey();
        String apiKey = configurationOptions.getApiKey();
        String apiUrl = "https://api.fireblocks.io/v1" + url;
        OkHttpClient client = new OkHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(new URI(apiUrl));
        Algorithm signingAlgorithm = Algorithm.RSA256(null, getPrivateKey(secretKey));
        Request request;
        if ("GET".equals(method)) {
            request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "fireblocks/sdk/java/2.0.0 (Windows 11 10.0; amd64)")
                    .addHeader("X-API-Key", apiKey)
                    .addHeader("Authorization", "Bearer " + signJwt(builder, apiKey, signingAlgorithm))
                    .build();
        } else {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            String json = "{}";  // 你的JSON数据
            RequestBody body = RequestBody.create(json, JSON);
            request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "fireblocks/sdk/java/2.0.0 (Windows 11 10.0; amd64)")
                    .addHeader("X-API-Key", apiKey)
                    .addHeader("Authorization", "Bearer " + signJwt(builder, apiKey, signingAlgorithm))
                    .build();
        }

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    private static String signJwt(HttpRequest.Builder builder, String apiKey, Algorithm signingAlgorithm) throws
            NoSuchAlgorithmException {
        HttpRequest request = builder.build();
        String path =
                request.uri().getPath()
                        + Optional.ofNullable(request.uri().getQuery())
                        .map(query -> "?" + query)
                        .orElse("");
        System.out.println(path);

        byte[] bytes =
                request.bodyPublisher()
                        .map(
                                p -> {
                                    HttpResponse.BodySubscriber<String> bodySubscriber =
                                            HttpResponse.BodySubscribers.ofString(
                                                    StandardCharsets.UTF_8);
                                    Flow.Subscriber<ByteBuffer> subscriber =
                                            new Flow.Subscriber<>() {
                                                @Override
                                                public void onSubscribe(
                                                        Flow.Subscription subscription) {
                                                    bodySubscriber.onSubscribe(subscription);
                                                }

                                                @Override
                                                public void onNext(ByteBuffer item) {
                                                    bodySubscriber.onNext(List.of(item));
                                                }

                                                @Override
                                                public void onError(Throwable throwable) {
                                                    bodySubscriber.onError(throwable);
                                                }

                                                @Override
                                                public void onComplete() {
                                                    bodySubscriber.onComplete();
                                                }
                                            };
                                    p.subscribe(subscriber);
                                    return bodySubscriber.getBody().toCompletableFuture().join();
                                })
                        .orElse("")
                        .getBytes();
        bytes = MessageDigest.getInstance("SHA-256").digest(bytes);
        String bodyHash = bytesToHex(bytes);

        Instant now = InstanceTimeWrapper.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(55));
        return JWT.create()
                .withClaim("uri", path)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("iat", issuedAt.getTime() / 1000)
                .withClaim("exp", expiresAt.getTime() / 1000)
                .withClaim("sub", apiKey)
                .withClaim("bodyHash", bodyHash)
                .sign(signingAlgorithm);
    }


    private static RSAPrivateKey getPrivateKey(String secretKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyContent =
                new String(secretKey.getBytes(), StandardCharsets.UTF_8)
                        .replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");
        byte[] contentBytes = Base64.getDecoder().decode(keyContent);
        return (RSAPrivateKey)
                KeyFactory.getInstance("RSA")
                        .generatePrivate(new PKCS8EncodedKeySpec(contentBytes));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}
