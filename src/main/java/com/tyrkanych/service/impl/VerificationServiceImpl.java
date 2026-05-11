package com.tyrkanych.service.impl;

import com.tyrkanych.service.EmailService;
import com.tyrkanych.service.VerificationService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final Map<String, String> codes = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> expiry = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    @Override
    public void generateAndSendCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        codes.put(email, code);
        expiry.put(email, LocalDateTime.now().plusMinutes(10));
        emailService.sendVerificationCode(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        if (!codes.containsKey(email)) {
            return false;
        }
        if (LocalDateTime.now().isAfter(expiry.get(email))) {
            clearCode(email);
            return false;
        }
        return codes.get(email).equals(code);
    }

    @Override
    public void clearCode(String email) {
        codes.remove(email);
        expiry.remove(email);
    }
}