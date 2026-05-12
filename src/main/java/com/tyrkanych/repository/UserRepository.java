package com.tyrkanych.repository;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRepository extends GenericRepository<User, Long> {

    @Autowired
    public UserRepository(UserDaoImpl userDaoImpl) {
        super(userDaoImpl); // Spring інжектує userDaoImpl з налаштованим DataSource
    }
}