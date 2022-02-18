/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Jake Yeo
 */
public class EncryptionDecryption {

    private SecretKey key;
    private int KEY_SIZE = 128;
    private Cipher encryptionCipher;
    private int T_LEN = 128;

    Cipher ecipher;
    Cipher dcipher;

    public EncryptionDecryption() {
        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(KEY_SIZE);
            this.key = generator.generateKey();
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception ex) {
            Logger.getLogger(EncryptionDecryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EncryptionDecryption(SecretKey key) {
        this.key = key;
        try {
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception ex) {
            Logger.getLogger(EncryptionDecryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String encrypt(String str) throws Exception {
        // Encode the string into bytes using utf-8
        byte[] utf8 = str.getBytes("UTF8");

        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);

        // Encode bytes to base64 to get a string
        return encode(enc);
    }

    public String decrypt(String str) throws Exception {
        // Decode base64 to get bytes
        byte[] dec = decode(str);

        byte[] utf8 = dcipher.doFinal(dec);

        // Decode using utf-8
        return new String(utf8, "UTF8");
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public SecretKey getSecretKey() {
        return this.key;
    }

    public void setSecretKey(SecretKey key) {
        this.key = key;
    }

    public SecretKey stringToSecretKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
