package flutterwave.com.rave.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import flutterwave.com.rave.models.BaseRequestData;

/**
 * Created by Shittu on 22/12/2016.
 */

public class RaveUtil {

    private static final String PBF_PUB_KEY = "PBFPubKey";
    private static final String CLIENT = "client";
    private static final String ALG = "alg";
    private static final String TX_REF = "txRef";
    private static final String TRANSACTION_REF = "transaction_reference";
    private static final String OTP = "otp";
    private static final String ALGORITHM = "DESede";
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";
    private static final String TARGET = "FLWSECK-";
    private static final String MD5 = "MD5";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String INVALID_ARGUMENT = "Invalid Argument";
    private static final String UTF_8 = "utf-8";

    public static String addPadding(String t, String s, int num) {
        StringBuilder retVal;

        if (null == s || 0 >= num) {
            throw new IllegalArgumentException(INVALID_ARGUMENT);
        }

        if (s.length() <= num) {
            return s.concat(t);
        }

        retVal = new StringBuilder(s);

        for (int i = retVal.length(); i > 0; i -= num) {
            retVal.insert(i, t);
        }
        return retVal.toString();
    }


    public static String cleanText(String text, String removeCharacter) {
        return text.trim().replace(removeCharacter, "");
    }

    public static String getJsonStringFromRequestData(BaseRequestData requestData) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new CustomPropertyNamingStrategy());
        try {
            return mapper.writeValueAsString(requestData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> getMapFromJsonReader(Reader reader) {
        return getMapFromJsonStringOrReader(reader, null);
    }

    public static Map<String, Object> getMapFromJsonString(String jsonString) {
        return getMapFromJsonStringOrReader(null, jsonString);
    }

    private static Map<String, Object> getMapFromJsonStringOrReader(Reader reader , String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        Map map = new HashMap<>();
        // convert JSON string to Map
        try {
            if(jsonString != null) {
                map = mapper.readValue(jsonString, Map.class);
            } else {
                map = mapper.readValue(reader, Map.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> buildChargeRequestParam(String key, String data) {
        //set request params
        Map<String, String> params = new HashMap<>();
        params.put(PBF_PUB_KEY, key);
        params.put(CLIENT, data);
        params.put(ALG, "3DES-24");

        return params;
    }

    public static Map<String, String> buildValidateRequestParam(String key, String txRef, String otp){
        //set request params
        Map<String, String> params = new HashMap<>();
        params.put(PBF_PUB_KEY, key);
        params.put(TX_REF, txRef);
        params.put(OTP, otp);

        return params;
    }

    public static Map<String, String> buildValidateChargeRequestParam(String key, String txRef, String otp){
        //set request params
        Map<String, String> params = new HashMap<>();
        params.put(PBF_PUB_KEY, key);
        params.put(TRANSACTION_REF, txRef);
        params.put(OTP, otp);

        return params;
    }

    public static String getEncryptedData(String unEncryptedString, String secret) {
        try {
            // hash the secret
            String md5Hash = getMd5(secret);
            String cleanSecret = secret.replace(TARGET, "");
            int hashLength = md5Hash.length();
            String encryptionKey = cleanSecret.substring(0, 12).concat(md5Hash.substring(hashLength - 12, hashLength));

            return encrypt(unEncryptedString, encryptionKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String encrypt(String data, String key) throws Exception {
        byte[] keyBytes = key.getBytes(UTF_8);
        SecretKeySpec skey = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(Cipher.ENCRYPT_MODE, skey);
        byte[] plainTextBytes = data.getBytes(UTF_8);
        byte[] buf = cipher.doFinal(plainTextBytes);
        byte[] base64Bytes = Base64.encodeBase64(buf);
        String base64EncryptedString = new String(base64Bytes);

        return base64EncryptedString;
    }

    private static String getMd5(String md5) throws Exception {
        MessageDigest md = java.security.MessageDigest.getInstance(MD5);
        byte[] array = md.digest(md5.getBytes(CHARSET_NAME));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

}
