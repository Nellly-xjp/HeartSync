package com.tyrkanych.session;

import com.tyrkanych.dto.UserDto;
import com.tyrkanych.viewmodel.UserViewModel;
import org.springframework.stereotype.Component;

/**
 * Singleton через Spring IoC — зберігає сесію поточного користувача. НЕ використовує статичний
 * getInstance() — Spring керує lifecycle.
 */
@Component
public class SessionManager {

    private final UserViewModel viewModel = new UserViewModel();
    private UserDto currentUser;

    public void login(UserDto user) {
        this.currentUser = user;
        viewModel.fromDto(user);
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public UserDto getCurrentUser() {
        return currentUser;
    }

    public UserViewModel getViewModel() {
        return viewModel;
    }

    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
}