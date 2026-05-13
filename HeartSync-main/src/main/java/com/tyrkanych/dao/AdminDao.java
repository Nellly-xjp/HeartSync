package com.tyrkanych.dao;

import com.tyrkanych.entity.Admin;
import java.util.Optional;

public interface AdminDao extends BaseDao<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);
}