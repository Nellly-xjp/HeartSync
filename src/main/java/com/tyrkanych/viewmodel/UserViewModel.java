package com.tyrkanych.viewmodel;

import com.tyrkanych.dto.UserDto;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class UserViewModel {

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty city = new SimpleStringProperty("");
    private final StringProperty bio = new SimpleStringProperty("");
    private final StringProperty gender = new SimpleStringProperty("");
    private final IntegerProperty age = new SimpleIntegerProperty(0);
    private final StringProperty initial = new SimpleStringProperty("?");
    private final StringProperty photoPath = new SimpleStringProperty("");
    private Long userId;

    public StringProperty photoPathProperty() {
        return photoPath;
    }

    public String getPhotoPath() {
        return photoPath.get();
    }

    public void setPhotoPath(String v) {
        photoPath.set(v != null ? v : "");
    }

    public void fromDto(UserDto dto) {

        if (dto == null) {
            return;
        }
        userId = dto.getId();
        name.set(dto.getName() != null ? dto.getName() : "");
        email.set(dto.getEmail() != null ? dto.getEmail() : "");
        city.set(dto.getCity() != null ? dto.getCity() : "");
        bio.set(dto.getBio() != null ? dto.getBio() : "");
        gender.set(dto.getGender() != null ? dto.getGender() : "");
        age.set(dto.getAge() != null ? dto.getAge() : 0);
        photoPath.set(dto.getPhotoPath() != null ? dto.getPhotoPath() : "");
        initial.set(name.get().isBlank() ? "?"
                : String.valueOf(name.get().charAt(0)).toUpperCase());
    }

    // Properties для MVVM binding
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty cityProperty() {
        return city;
    }

    public StringProperty bioProperty() {
        return bio;
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public StringProperty initialProperty() {
        return initial;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String v) {
        name.set(v);
    }

    public String getEmail() {
        return email.get();
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String v) {
        city.set(v);
    }

    public String getBio() {
        return bio.get();
    }

    public void setBio(String v) {
        bio.set(v);
    }

    public String getGender() {
        return gender.get();
    }

    public int getAge() {
        return age.get();
    }

    public String getInitial() {
        return initial.get();
    }

    public boolean isLoaded() {
        return userId != null;
    }

}