package com.tyrkanych.repository;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;

public class UserRepository extends GenericRepository<User, Long> {

    public UserRepository() {
        super(new UserDaoImpl());
    }
}