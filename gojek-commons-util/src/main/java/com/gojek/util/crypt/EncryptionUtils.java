/**
 *
 */
package com.gojek.util.crypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.core.CoreException;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/**
 * @author ganeshs
 *
 */
public class EncryptionUtils {

    private static final String ALGORITHM_AES256 = "AES/CBC/PKCS5Padding";
    
    private static SecretKeySpec secretKeySpec;
    
    private static Cipher cipher;
    
    private static final byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    
    private static IvParameterSpec ivSpec = new IvParameterSpec(iv);
    
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);
    
    /**
     * Initializes the key 
     *
     * @param key
     */
    public static void init(String key) {
        try {
            secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            cipher = Cipher.getInstance(ALGORITHM_AES256);
        } catch (Exception e) {
            logger.error("Failed while creating the secret key", e);
            throw new CoreException("Failed while creating the secret key", e);
        }
    }

    /**
     * Takes message and encrypts with Key
     *
     * @param message String
     * @return String Base64 encoded
     */
    public static String encrypt(String message) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encryptedTextBytes = cipher.doFinal(message.getBytes("UTF-8"));
            return BaseEncoding.base64().encode(encryptedTextBytes);
        } catch (Exception e) {
            logger.error("Failed while encrypting the message", e);
            throw new CoreException("Failed while encrypting the message", e);
        }
    }

    /**
     * Takes Base64 encoded String and decodes with provided key
     *
     * @param message String encoded with Base64
     * @return String
     */
    public static String decrypt(String message) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encryptedTextBytes = BaseEncoding.base64().decode(message);
            byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
            return new String(decryptedTextBytes);
        } catch (Exception e) {
            logger.error("Failed while decrypting the message", e);
            throw new CoreException("Failed while decrypting the message", e);
        }
    }
    
    /**
     * Md5 encrypts the given value
     *
     * @param value
     * @return
     */
    public static String md5Encrypt(String value) {
        return Hashing.md5().newHasher().putString(value, Charsets.UTF_8).hash().toString();
    }
    
    public static void main(String[] args) {
        EncryptionUtils.init("test123test12312");
        String encrypted = EncryptionUtils.encrypt("abcdefg");
        System.out.println(encrypted);
        System.out.println(EncryptionUtils.decrypt(encrypted));
    }
}