package com.tyrkanych.dao;

import com.tyrkanych.entity.Preferences;
import java.util.Optional;

public interface PreferencesDao extends BaseDao<Preferences, Long> {

    Optional<Preferences> findByUserId(Long userId);

    void updateByUserId(Preferences preferences);
}