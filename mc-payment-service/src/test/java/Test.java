import com.mc.payment.core.service.util.AKSKUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Test
 *
 * @author GZM
 * @since 2024/10/21 上午10:36
 */
public class Test {

    public static String calculateHmacSha1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKeySpec);
        byte[] result = mac.doFinal(data.getBytes());

        System.out.println("result:"+result);

        // Convert the byte array to a hexadecimal string
        Formatter formatter = new Formatter();
        for (byte b : result) {
            formatter.format("%02x", b);
        }
        String hexString = formatter.toString();
        formatter.close();

        return hexString;
    }

    public static void main(String[] args) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String ak="a9cd3b5257ce4d1894bca46d7f7932ec";
        String sk="e4bb437397334985ba88508692cd5d69";
        String requestURI="/openapi/v1/withdrawal/request";
        Map<String, String> map = new HashMap<>();
        System.out.println(timestamp);
        System.out.println(AKSKUtil.calculateHMAC(ak + timestamp + requestURI, sk));


    }

}
