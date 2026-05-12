package com.tyrkanych.entity;

import java.util.Objects;

public class UserInterest {

    private Long userId;
    private Long interestId;
    private Integer level = 1;

    public UserInterest() {
    }

    public UserInterest(Long userId, Long interestId, Integer level) {
        this.userId = userId;
        this.interestId = interestId;
        this.level = level;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInterestId() {
        return interestId;
    }

    public void setInterestId(Long interestId) {
        this.interestId = interestId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserInterest that = (UserInterest) o;
        return Objects.equals(userId, that.userId) && Objects.equals(interestId, that.interestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, interestId);
    }
}