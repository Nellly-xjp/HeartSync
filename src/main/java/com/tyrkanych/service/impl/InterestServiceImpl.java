package com.tyrkanych.service.impl;

import com.tyrkanych.dao.InterestDao;
import com.tyrkanych.entity.Interest;
import com.tyrkanych.service.InterestService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InterestServiceImpl implements InterestService {

    private final InterestDao interestDao;

    @Autowired
    public InterestServiceImpl(InterestDao interestDao) {
        this.interestDao = interestDao;
    }

    @Override
    public Interest createInterest(String name) {
        if (interestDao.existsByName(name)) {
            throw new IllegalArgumentException("Interest already exists");
        }

        Interest interest = new Interest(name);
        return interestDao.save(interest);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        return interestDao.findByName(name);
    }

    @Override
    public List<Interest> search(String keyword) {
        return interestDao.findByNameContaining(keyword);
    }

    @Override
    public List<Interest> findAll() {
        return interestDao.findAll();
    }
}