package com.tyrkanych.service;

import com.tyrkanych.dao.impl.ReportDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Report;
import com.tyrkanych.entity.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserDaoImpl userDao;
    private final ReportDaoImpl reportDao;

    @Autowired
    public AdminService(UserDaoImpl userDao, ReportDaoImpl reportDao) {
        this.userDao = userDao;
        this.reportDao = reportDao;
    }

    public void banUser(Long userId) {
        userDao.setBanned(userId, true);
    }

    public void unbanUser(Long userId) {
        userDao.setBanned(userId, false);
    }

    public List<User> getBannedUsers() {
        return userDao.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsBanned()))
                .toList();
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public List<Report> getAllReports() {
        return reportDao.findAll();
    }

    public long getTotalUsers() {
        return userDao.findAll().size();
    }

    public long getTotalReports() {
        return reportDao.findAll().size();
    }

    public long getBannedCount() {
        return getBannedUsers().size();
    }
}