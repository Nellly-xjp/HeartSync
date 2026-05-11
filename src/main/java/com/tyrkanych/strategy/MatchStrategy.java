package com.tyrkanych.strategy;

import com.tyrkanych.entity.User;

public interface MatchStrategy {

    double calculateCompatibility(User user1, User user2);
}