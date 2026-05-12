package com.tyrkanych.service.impl;

import com.tyrkanych.dao.UserDao;
import com.tyrkanych.dto.UserDto;
import com.tyrkanych.dto.UserRegistrationDto;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.UserService;
import com.tyrkanych.service.ValidationService;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ValidationService validationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao,
            ValidationService validationService,
            PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.validationService = validationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto register(UserRegistrationDto dto) {
        validationService.validateRegistration(dto);

        // Хешуємо пароль перед збереженням
        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .email(dto.getEmail())
                .password(hashedPassword)
                .name(dto.getName())
                .gender(dto.getGender())
                .birthDate(dto.getBirthDate())
                .city(dto.getCity())
                .bio(dto.getBio() != null ? dto.getBio() : "")
                .build();

        User savedUser = userDao.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        return userDao.findById(id).map(this::convertToDto);
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        return userDao.findByEmail(email).map(this::convertToDto);
    }

    @Override
    public Optional<UserDto> findByEmailAndPassword(String email, String password) {
        return userDao.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(this::convertToDto);
    }

    @Override
    public List<UserDto> findAll() {
        return userDao.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setGender(user.getGender());
        dto.setBirthDate(user.getBirthDate());
        dto.setCity(user.getCity());
        dto.setBio(user.getBio());
        dto.setPhotoPath(user.getPhotoPath());
        if (user.getBirthDate() != null) {
            dto.setAge(Period.between(user.getBirthDate(), LocalDate.now()).getYears());
        }
        return dto;
    }
}