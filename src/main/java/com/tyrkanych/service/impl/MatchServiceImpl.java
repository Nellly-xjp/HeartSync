package com.tyrkanych.service.impl;

import com.tyrkanych.dao.MatchDao;
import com.tyrkanych.dao.UserDao;
import com.tyrkanych.entity.Match;
import com.tyrkanych.service.MatchService;
import com.tyrkanych.strategy.MatchStrategy;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchDao matchDao;
    private final UserDao userDao;
    private final MatchStrategy matchStrategy;

    @Autowired
    public MatchServiceImpl(MatchDao matchDao, UserDao userDao, MatchStrategy matchStrategy) {
        this.matchDao = matchDao;
        this.userDao = userDao;
        this.matchStrategy = matchStrategy;
    }

    @Override
    public Match createMatch(Long user1Id, Long user2Id, Double compatibilityScore) {
        if (matchDao.findByUsers(user1Id, user2Id).isPresent()) {
            throw new IllegalArgumentException("Match already exists between these users");
        }

        double score = (compatibilityScore != null)
                ? compatibilityScore
                : matchStrategy.calculateCompatibility(
                        userDao.findById(user1Id)
                                .orElseThrow(() -> new IllegalArgumentException("User1 not found")),
                        userDao.findById(user2Id)
                                .orElseThrow(() -> new IllegalArgumentException("User2 not found"))
                );

        Match match = new Match(user1Id, user2Id, score);
        return matchDao.save(match);
    }

    @Override
    public Optional<Match> findByUsers(Long user1Id, Long user2Id) {
        return matchDao.findByUsers(user1Id, user2Id);
    }

    @Override
    public List<Match> findByUserId(Long userId) {
        return matchDao.findByUserId(userId);
    }

    @Override
    public List<Match> findAll() {
        return matchDao.findAll();
    }
}