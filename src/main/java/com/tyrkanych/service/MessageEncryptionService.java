package com.tyrkanych.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class MessageEncryptionService {

    // 32 символи = AES-256
    private static final String SECRET_KEY =
            "HeartSync2026SecretKey123456789!";

    public String encrypt(String plainText) {
        try {
            SecretKey key = new SecretKeySpec(
                    SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(
                    plainText.getBytes());
            return Base64.getEncoder()
                    .encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Помилка шифрування: " + e.getMessage());
        }
    }

    public String decrypt(String encryptedText) {
        try {
            SecretKey key = new SecretKeySpec(
                    SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder()
                    .decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Помилка дешифрування: " + e.getMessage());
        }
    }
}