package com.tyrkanych.dao;

import com.tyrkanych.entity.Like;
import java.util.List;
import java.util.Optional;

public interface LikeDao extends BaseDao<Like, Long> {

    Optional<Like> findByUsers(Long fromUserId, Long toUserId);

    List<Like> findByFromUserId(Long fromUserId);

    List<Like> findByToUserId(Long toUserId);

    boolean existsLike(Long fromUserId, Long toUserId);

    void deleteLike(Long fromUserId, Long toUserId);
}