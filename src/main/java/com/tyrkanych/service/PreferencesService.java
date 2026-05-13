package com.tyrkanych.service;

import com.tyrkanych.entity.Preferences;
import java.util.Optional;

public interface PreferencesService {

    Preferences save(Preferences preferences);

    Optional<Preferences> findByUserId(Long userId);

    void update(Preferences preferences);
}