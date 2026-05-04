package com.tyrkanych.entity;

import java.time.LocalDateTime;

public class Match {

    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Double compatibilityScore;
    private LocalDateTime createdAt;

    public Match() {
    }

    public Match(Long user1Id, Long user2Id, Double compatibilityScore) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.compatibilityScore = compatibilityScore;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public Double getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(Double compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}