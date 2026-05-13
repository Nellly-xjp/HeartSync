package com.tyrkanych.service;

public interface EmailService {

    void sendVerificationCode(String toEmail, String code);
}