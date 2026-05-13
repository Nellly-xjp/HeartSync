package com.tyrkanych.dao;

import com.tyrkanych.entity.UserInterest;
import java.util.List;

public interface UserInterestDao {

    void save(UserInterest userInterest);

    void saveAll(List<UserInterest> userInterests);

    List<UserInterest> findByUserId(Long userId);

    List<UserInterest> findByInterestId(Long interestId);

    void deleteByUserId(Long userId);

    void delete(Long userId, Long interestId);

    boolean exists(Long userId, Long interestId);
}