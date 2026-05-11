package com.tyrkanych.service.impl;

import com.tyrkanych.dao.PreferencesDao;
import com.tyrkanych.entity.Preferences;
import com.tyrkanych.service.PreferencesService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PreferencesServiceImpl implements PreferencesService {

    private final PreferencesDao preferencesDao;

    @Autowired
    public PreferencesServiceImpl(PreferencesDao preferencesDao) {
        this.preferencesDao = preferencesDao;
    }

    @Override
    public Preferences save(Preferences preferences) {
        return preferencesDao.save(preferences);
    }

    @Override
    public Optional<Preferences> findByUserId(Long userId) {
        return preferencesDao.findByUserId(userId);
    }

    @Override
    public void update(Preferences preferences) {
        preferencesDao.updateByUserId(preferences);
    }
}