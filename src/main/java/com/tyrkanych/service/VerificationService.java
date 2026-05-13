package com.tyrkanych.service;

public interface VerificationService {

    void generateAndSendCode(String email);

    boolean verifyCode(String email, String code);

    void clearCode(String email);
}