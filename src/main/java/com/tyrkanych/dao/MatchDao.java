package com.tyrkanych.dao;

import com.tyrkanych.entity.Match;
import java.util.List;
import java.util.Optional;

public interface MatchDao extends BaseDao<Match, Long> {

    Optional<Match> findByUsers(Long user1Id, Long user2Id);

    List<Match> findByUserId(Long userId);
}