package com.birdben.utils.secret;

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class SecurityHelperTest extends TestCase {

    // URL加密参数（客户端传递的参数）
    public static final String loginToken = "1234567890";
    public static final String timestamp = "1447330876315";

    // 客户端经过MAC算法加密之后的参数（客户端传递的参数）
    public static final String secret = "k3HhZ5w3wAm4VXklNUsVxS1hTsE=";

    @Test
    public void testSecurityHelper() {
        String srcSource = loginToken + timestamp;
        byte[] secretData = new byte[0];
        try {
            secretData = SecurityHelper.HmacSHA1Encrypt(srcSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String secretString = Base64.encodeBase64String(secretData);
        System.out.println("srcSource:" + srcSource);
        System.out.println("server secret:" + secretString);
        System.out.println("client secret:" + secret);
    }

} 