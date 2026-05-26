package com.tyrkanych.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

public class User {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String city;
    private String bio;
    private LocalDateTime createdAt;
    private String photoPath;
    private String role;
    private Boolean isBanned;
    private String interests;

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    public User() {}

    public User(String email, String password, String name, String gender, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsBanned() { return isBanned; }
    public void setIsBanned(Boolean isBanned) { this.isBanned = isBanned; }

    public Integer getAge() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private Long id;
        private String email;
        private String password;
        private String name;
        private String gender;
        private LocalDate birthDate;
        private String city;
        private String bio;
        private LocalDateTime createdAt;
        private String photoPath;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder birthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder photoPath(String photoPath) { this.photoPath = photoPath; return this; }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.email = this.email;
            user.password = this.password;
            user.name = this.name;
            user.gender = this.gender;
            user.birthDate = this.birthDate;
            user.city = this.city;
            user.bio = this.bio;
            user.createdAt = this.createdAt;
            user.photoPath = this.photoPath;
            return user;
        }
    }
}