package com.tyrkanych.service;

import com.tyrkanych.entity.Match;
import java.util.List;
import java.util.Optional;

public interface MatchService {

    Match createMatch(Long user1Id, Long user2Id, Double compatibilityScore);

    Optional<Match> findByUsers(Long user1Id, Long user2Id);

    List<Match> findByUserId(Long userId);

    List<Match> findAll();

}