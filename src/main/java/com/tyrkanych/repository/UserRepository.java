package com.tyrkanych.repository;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Репозиторій для User.
 * <p>
 * Виправлення: замість "super(new UserDaoImpl())" використовується Spring DI. new UserDaoImpl() не
 * отримував би @Autowired DataSource — тому Spring не міг би виконати жодного SQL-запиту через цей
 * репозиторій.
 */
@Component
public class UserRepository extends GenericRepository<User, Long> {

    @Autowired
    public UserRepository(UserDaoImpl userDaoImpl) {
        super(userDaoImpl); // Spring інжектує userDaoImpl з налаштованим DataSource
    }
}