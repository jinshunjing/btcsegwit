package org.jim.bitcoin.wallet;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * Encryption
 *
 * @author JSJ
 */
public class Encryption {
    /**
     * IV
     */
    private static byte[] DEFAULT_IV = new byte[] {67, 76, 85, 94, 13, 22, 31, 43, 52, 61, 75, 84, 93, 12, 21, 35};

    /**
     * Encode password to 128 bits
     *
     * @param password
     * @return
     */
    public static byte[] encodePassword(String password) {
        byte[] encoded256 = DigestUtils.sha256(password);
        return Arrays.copyOfRange(encoded256, 0, 16);
    }

    /**
     * Encrypt
     *
     * @param key
     * @param plain
     * @return
     */
    public static byte[] encrypt(byte[] key, byte[] plain) {
        byte[] data = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(DEFAULT_IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParams);
            data = cipher.doFinal(plain);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Decrypt
     *
     * @param key
     * @param data
     * @return
     */
    public static byte[] decrypt(byte[] key, byte[] data) {
        byte[] plain = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(DEFAULT_IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);
            plain = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plain;
    }

}
