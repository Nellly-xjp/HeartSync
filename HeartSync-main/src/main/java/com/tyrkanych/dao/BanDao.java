package com.tyrkanych.dao;

import com.tyrkanych.entity.Ban;
import java.util.List;
import java.util.Optional;

public interface BanDao extends BaseDao<Ban, Long> {

    Optional<Ban> findActiveBanByUserId(Long userId);

    List<Ban> findByUserId(Long userId);
}