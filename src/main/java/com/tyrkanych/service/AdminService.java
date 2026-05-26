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

    public String getUserName(Long userId) {
        if (userId == null) return "?";
        return userDao.findById(userId)
                .map(u -> u.getName() != null ? u.getName() : u.getEmail())
                .orElse("ID: " + userId);
    }
    public String getUserInfo(Long userId) {
        if (userId == null) return "?";
        return userDao.findById(userId)
                .map(u -> "ID:" + u.getId() + " " +
                        (u.getName() != null ? u.getName() : "?") +
                        " (" + u.getEmail() + ")")
                .orElse("ID: " + userId);
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
    public void exportToExcel(List<User> users, List<Report> reports, String filePath) {
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            // Аркуш користувачів
            var usersSheet = workbook.createSheet("Users");
            var header = usersSheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("City");
            header.createCell(4).setCellValue("Banned");

            int row = 1;
            for (User u : users) {
                var r = usersSheet.createRow(row++);
                r.createCell(0).setCellValue(u.getId());
                r.createCell(1).setCellValue(u.getName() != null ? u.getName() : "");
                r.createCell(2).setCellValue(u.getEmail() != null ? u.getEmail() : "");
                r.createCell(3).setCellValue(u.getCity() != null ? u.getCity() : "");
                r.createCell(4).setCellValue(Boolean.TRUE.equals(u.getIsBanned()) ? "Yes" : "No");
            }

            var reportsSheet = workbook.createSheet("Reports");
            var rHeader = reportsSheet.createRow(0);
            rHeader.createCell(0).setCellValue("ID");
            rHeader.createCell(1).setCellValue("From");
            rHeader.createCell(2).setCellValue("Target");
            rHeader.createCell(3).setCellValue("Reason");
            rHeader.createCell(4).setCellValue("Status");

            int rRow = 1;
            for (Report rep : reports) {
                var r = reportsSheet.createRow(rRow++);
                r.createCell(0).setCellValue(rep.getId());
                r.createCell(1).setCellValue(getUserInfo(rep.getFromUserId()));
                r.createCell(2).setCellValue(getUserInfo(rep.getReportedUserId()));
                r.createCell(3).setCellValue(rep.getReason() != null ? rep.getReason() : "");
                r.createCell(4).setCellValue(rep.getStatus() != null ? rep.getStatus() : "");
            }

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            throw new RuntimeException("Помилка Excel: " + e.getMessage(), e);
        }
    }
}