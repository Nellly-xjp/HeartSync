package com.tyrkanych.entity;

import java.util.Objects;

public class Preferences {

    private Long id;
    private Long userId;
    private String preferredGender;
    private Integer minAge = 18;
    private Integer maxAge = 100;
    private String city;


    public Preferences() {
    }

    public Preferences(Long userId) {
        this.userId = userId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(String preferredGender) {
        this.preferredGender = preferredGender;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Preferences that = (Preferences) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private String preferredGender;
        private Integer minAge = 18;
        private Integer maxAge = 100;
        private String city;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder preferredGender(String preferredGender) {
            this.preferredGender = preferredGender;
            return this;
        }

        public Builder minAge(Integer minAge) {
            this.minAge = minAge;
            return this;
        }

        public Builder maxAge(Integer maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Preferences build() {
            Preferences p = new Preferences();
            p.id = this.id;
            p.userId = this.userId;
            p.preferredGender = this.preferredGender;
            p.minAge = this.minAge;
            p.maxAge = this.maxAge;
            p.city = this.city;
            return p;
        }
    }
}