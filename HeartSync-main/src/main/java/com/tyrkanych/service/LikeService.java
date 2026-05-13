package com.tyrkanych.service;

import com.tyrkanych.entity.Like;
import java.util.List;

public interface LikeService {

    Like addLike(Long fromUserId, Long toUserId);

    void removeLike(Long fromUserId, Long toUserId);

    boolean existsLike(Long fromUserId, Long toUserId);

    List<Like> findByFromUserId(Long fromUserId);

    List<Like> findByToUserId(Long toUserId);
}