package com.tyrkanych.service;

import com.tyrkanych.dto.UserRegistrationDto;

/**
 * Інтерфейс для валідації бізнес-логіки.
 */
public interface ValidationService {

    void validateRegistration(UserRegistrationDto dto);

    void validateEmail(String email);

    void validateAge(int age);
}