package com.tyrkanych.service;

import com.tyrkanych.entity.Interest;
import java.util.List;
import java.util.Optional;

public interface InterestService {

    Interest createInterest(String name);

    Optional<Interest> findByName(String name);

    List<Interest> search(String keyword);

    List<Interest> findAll();
}