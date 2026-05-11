package com.tyrkanych.service.impl;

import com.tyrkanych.dao.LikeDao;
import com.tyrkanych.entity.Like;
import com.tyrkanych.service.LikeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeDao likeDao;

    @Autowired
    public LikeServiceImpl(LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    @Override
    public Like addLike(Long fromUserId, Long toUserId) {
        if (likeDao.existsLike(fromUserId, toUserId)) {
            throw new IllegalArgumentException("Like already exists");
        }

        Like like = new Like(fromUserId, toUserId);
        return likeDao.save(like);
    }

    @Override
    public void removeLike(Long fromUserId, Long toUserId) {
        likeDao.deleteLike(fromUserId, toUserId);
    }

    @Override
    public boolean existsLike(Long fromUserId, Long toUserId) {
        return likeDao.existsLike(fromUserId, toUserId);
    }

    @Override
    public List<Like> findByFromUserId(Long fromUserId) {
        return likeDao.findByFromUserId(fromUserId);
    }

    @Override
    public List<Like> findByToUserId(Long toUserId) {
        return likeDao.findByToUserId(toUserId);
    }
}