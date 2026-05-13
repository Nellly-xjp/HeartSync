package com.tyrkanych.strategy.impl;

import com.tyrkanych.dao.UserInterestDao;
import com.tyrkanych.entity.User;
import com.tyrkanych.entity.UserInterest;
import com.tyrkanych.strategy.MatchStrategy;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultMatchStrategy implements MatchStrategy {

    private final UserInterestDao userInterestDao;

    @Autowired
    public DefaultMatchStrategy(UserInterestDao userInterestDao) {
        this.userInterestDao = userInterestDao;
    }

    @Override
    public double calculateCompatibility(User user1, User user2) {
        double score = 0.0;

        if (user1.getCity() != null && user2.getCity() != null &&
                user1.getCity().equalsIgnoreCase(user2.getCity())) {
            score += 30;
        }

        if (user1.getBirthDate() != null && user2.getBirthDate() != null) {
            int age1 = Period.between(user1.getBirthDate(), LocalDate.now()).getYears();
            int age2 = Period.between(user2.getBirthDate(), LocalDate.now()).getYears();
            int diff = Math.abs(age1 - age2);
            if (diff <= 3) {
                score += 25;
            }
        }

        List<Long> interests1 = userInterestDao.findByUserId(user1.getId())
                .stream()
                .map(UserInterest::getInterestId)
                .collect(Collectors.toList());

        List<Long> interests2 = userInterestDao.findByUserId(user2.getId())
                .stream()
                .map(UserInterest::getInterestId)
                .collect(Collectors.toList());

        long commonInterests = interests1.stream()
                .filter(interests2::contains)
                .count();

        score += commonInterests * 10;

        return Math.min(score, 100.0);
    }
}