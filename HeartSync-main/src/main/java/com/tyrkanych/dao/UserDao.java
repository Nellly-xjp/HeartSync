package com.tyrkanych.dao;

import com.tyrkanych.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao extends BaseDao<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByCity(String city);

    List<User> findByGender(String gender);

    void updatePassword(Long userId, String newPassword);
}