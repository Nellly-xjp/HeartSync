package com.tyrkanych.service;

import com.tyrkanych.dto.UserDto;
import com.tyrkanych.dto.UserRegistrationDto;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto register(UserRegistrationDto dto);

    Optional<UserDto> findById(Long id);

    Optional<UserDto> findByEmail(String email);

    List<UserDto> findAll();

    boolean existsByEmail(String email);
}