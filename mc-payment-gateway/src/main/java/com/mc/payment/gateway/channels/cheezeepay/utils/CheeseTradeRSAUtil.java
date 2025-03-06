package com.mc.payment.gateway.channels.cheezeepay.utils;


import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class CheeseTradeRSAUtil {
    private static final String DATA = "data";

    // Generate a signature
    public static String getSign(Map<String, Object> params, String privateKey) throws Exception {
        params.remove("sign");
        String textContent = getContent(params);
        return sign(textContent, privateKey);
    }

    // Verify the signature
    public static boolean verifySign(Map<String, Object> params, String publickey) throws
            Exception {
        String platSign= (String)params.remove("sign");

        String textContent = getContent(params);
        return verify(textContent, platSign, publickey);
    }

    // Specifies the string for the reception signature
    private static String getContent(Map<String, Object> params) {
        List<String> paramNameList = new ArrayList<>(params.keySet());
        Collections.sort(paramNameList);
        StringBuilder stringBuilder = new StringBuilder();
        for (String name : paramNameList) {
            if(ObjectUtil.isNotEmpty(params.get(name))) {
                if (params.get(name) != null ) {
                    if (DATA.equals(name)){
                        String jsonString = getJsonStringByJackson(params.get(name));
                        stringBuilder.append(name).append("=").append(jsonString).append("&");
                    }else {
                        stringBuilder.append(name).append("=").append(params.get(name)).append("&");
                    }
                }
            }
        }
        String content = stringBuilder.toString();
        content = content.substring(0, content.length() - 1);
        return content;
    }

    // Jackson serialization object
    private static String getJsonStringByJackson(Object param) {
        // Serialize objects using Jackson method
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(param);
        } catch (Exception ex) {
            return null;
        }
    }

    // Use the private key to generate the signature
    public static String sign(String message, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(privateKey);
        signer.update(message.getBytes());
        byte[] signatureBytes = signer.sign();
        return java.util.Base64.getEncoder().encodeToString(signatureBytes);
    }

    // Use a public key to check
    public static boolean verify(String message, String signatureString, String publicKeyString) throws Exception {
        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(publicKeyString);
        byte[] signatureBytes = java.util.Base64.getDecoder().decode(signatureString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(message.getBytes());
        return verifier.verify(signatureBytes);
    }


    public static void main(String[] args) throws Exception {

        // RSA public key
        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv8ICamtlh1E6ycHa7TooPJcLij2i+Otv9axeOafsYtubqBlBMojrQdDguhy3j1H5BmMz1Czx/ztMheRplpIUrs95Siv17V0nkXUCMAEO5WJYX3SY0y1Qu5sJA0WXhXj8G5wF+aQtIemMX9Im4wIJZFtneOQLHKLmbfdcSMYH9FSzWfXR3vEe4/ITyjhqTYvu9PU4ZkWUFR0CzfusPpqyA+yclgUm239m1VnO1AZRwpLncIxIlv6/egnn07pG9EooGktF6alhCmB3jVktAz/2uTlA81zIun6hxMzD2urjWGy6Tlta8TITIVPe1vKS2AW2tE/QSOxf8brKM8VQ1XkGwwIDAQAB";

        // RSA private key
        String privateKeyString = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC/wgJqa2WHUTrJwdrtOig8lwuKPaL462/1rF45p+xi25uoGUEyiOtB0OC6HLePUfkGYzPULPH/O0yF5GmWkhSuz3lKK/XtXSeRdQIwAQ7lYlhfdJjTLVC7mwkDRZeFePwbnAX5pC0h6Yxf0ibjAglkW2d45AscouZt91xIxgf0VLNZ9dHe8R7j8hPKOGpNi+709ThmRZQVHQLN+6w+mrID7JyWBSbbf2bVWc7UBlHCkudwjEiW/r96CefTukb0SigaS0XpqWEKYHeNWS0DP/a5OUDzXMi6fqHEzMPa6uNYbLpOW1rxMhMhU97W8pLYBba0T9BI7F/xusozxVDVeQbDAgMBAAECggEAdZ4EaU3yemuCiZoUNIoFgBSNiX+A5PlUNPZC3U54mbJl6VeEPADre/Uowj82//uhqR9T/QKMdKbkqwONGEQF16t+k9YfBDatPHTuoI8lmeEWn4Ye7vjOmiPgBVe8NqwcxrqOl67x1+kupt955qerJxlBgE8v2aK5gB3HRwPggSYZgSZJ3X2zQMJ+XN4qe49Un+utqFuGtVRwPQvs/Kazp5GL7WlR0OnpQ2KZ+ur9T58VfTKmIeqBckey7nIrb/fdodJ7IqcykWjSLmy8p84W78iiRDfPn81Ujb231EWW+AbuV399aXBZ4oaR4q8wwkhe9xHUADWLvcfCnMrB40LkEQKBgQD2TEeBEB5Gwj/zpriq23xKJyzfo0hOxzGY/Ol2EykGeEYhny/YiTlMYtOMy37RiGKr4lN7G/pKl2fI5kRjgQM1QXgx6dxd11D0rORXj+SAy6+zAq3NVY4c3zDKmrQQ8x3f2QFffo+TmLtw/BH0Frp+a2lzKzIcXtZ4/akJdVIihwKBgQDHT7xvOaNyAPnnYwpROMI9eUVgFc9pMRAmQ18at8ggZ8M+Ww1nhr00loufzkovCb9MvGR4HSedWHfgmtDu1hUFtl7PItNItsB347yWPxR/lVLs44NVXt7jkFisoBoEBXaNVVt6uOSvBiqINmX3gm4FoisI2SRZsH7nMr07OXO85QKBgCkXdN6Nh9+aTP0jla+7yrK8cnolTc0G4rl5iKHQdInFyz2Ux3DDBDJAUrcsxE3FCWFP80mY01tag1A7SrGnNpfOAnWhZMirQbBwn/AT67WpRfkBu2mEmp8qhYNn6B7j0Nol5FrATMf9NViWVZB5skehOAik7klZcULiXy9ayYdFAoGAA+0bLim5gYZpZdh6nIz8ro9UAYeMdWsFAWv0VKdoJtNEclcC1ZY+5elNSNGcfn63qQBRDlisrhnPCrqiAQJwmZM1HOl3tgf7lKEE1wTZF4ZOguIcdTMQOvVd+dCRkqC07CXRToKT5qq9bb98lLqaBjxikqMMZ0PlzBIgzijckc0CgYAH16CziStEZVvKbiR8b0ftF9mf6V8I0KHianMgV8X6Y51aE0Ig1eQhIvqSjqtBac7gEppiu3OQVrp39uLdgyvDSVta1Hq9QYYTDNt8crt69aD/Th1h5esYvApYyZIhHbFDR5meE4Y3nHpfY7J8zhmfyaVW/6cuwPaToTtrEjQmWQ==";

        // Request interface parameters
        Map<String, Object> params  = new HashMap<>();
        params.put("a","A");
        params.put("b","BB");

        // Use the private key to generate the signature
        String platSign = getSign(params, privateKeyString);
        System.out.println("sign: " + platSign);
        params.put("sign",platSign);

        // Use a public key to verify the signature
        boolean verified = verifySign( params, publicKeyString);
        System.out.println("Signature verified: " + verified);

    }


}
