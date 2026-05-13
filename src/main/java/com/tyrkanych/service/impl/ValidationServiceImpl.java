package com.tyrkanych.service.impl;

import com.tyrkanych.dto.UserRegistrationDto;
import com.tyrkanych.service.ValidationService;
import java.time.LocalDate;
import java.time.Period;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateRegistration(UserRegistrationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO не може бути null");
        }

        validateEmail(dto.getEmail());

        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Пароль повинен містити мінімум 6 символів");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ім'я не може бути порожнім");
        }
        if (dto.getBirthDate() == null) {
            throw new IllegalArgumentException("Дата народження є обов'язковою");
        }

        int age = Period.between(dto.getBirthDate(), LocalDate.now()).getYears();
        validateAge(age);
    }

    @Override
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не може бути порожнім");
        }
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Невірний формат email: " + email);
        }
    }

    @Override
    public void validateAge(int age) {
        if (age < 18) {
            throw new IllegalArgumentException("Користувач повинен бути старше 18 років");
        }
        if (age > 120) {
            throw new IllegalArgumentException("Невірна дата народження");
        }
    }
}