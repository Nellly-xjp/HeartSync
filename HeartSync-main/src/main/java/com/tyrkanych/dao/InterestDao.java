package com.tyrkanych.dao;

import com.tyrkanych.entity.Interest;
import java.util.List;
import java.util.Optional;

public interface InterestDao extends BaseDao<Interest, Long> {

    Optional<Interest> findByName(String name);

    List<Interest> findByNameContaining(String keyword);

    boolean existsByName(String name);
}