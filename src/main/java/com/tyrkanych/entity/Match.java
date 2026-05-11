package com.tyrkanych.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Match {

    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Double compatibilityScore;
    private LocalDateTime createdAt;

    // ==================== КОНСТРУКТОРИ ====================

    public Match() {
    }

    public Match(Long user1Id, Long user2Id, Double compatibilityScore) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.compatibilityScore = compatibilityScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    // ==================== GETTERS & SETTERS ====================

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Match{id=" + id + ", user1Id=" + user1Id
                + ", user2Id=" + user2Id
                + ", score=" + compatibilityScore + "}";
    }

    public static class Builder {

        private Long id;
        private Long user1Id;
        private Long user2Id;
        private Double compatibilityScore;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user1Id(Long user1Id) {
            this.user1Id = user1Id;
            return this;
        }

        public Builder user2Id(Long user2Id) {
            this.user2Id = user2Id;
            return this;
        }

        public Builder compatibilityScore(Double compatibilityScore) {
            this.compatibilityScore = compatibilityScore;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Match build() {
            Match m = new Match();
            m.id = this.id;
            m.user1Id = this.user1Id;
            m.user2Id = this.user2Id;
            m.compatibilityScore = this.compatibilityScore;
            m.createdAt = this.createdAt;
            return m;
        }
    }
}