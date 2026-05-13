package com.tyrkanych.service.impl;

import com.tyrkanych.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("HeartSync — Код підтвердження");
        message.setText(
                "Вітаємо в HeartSync! ♥\n\n" +
                        "Ваш код підтвердження: " + code + "\n\n" +
                        "Код дійсний 10 хвилин.\n\n" +
                        "З повагою, команда HeartSync"
        );
        mailSender.send(message);
    }
}