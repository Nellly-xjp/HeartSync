package com.tyrkanych.service;

import com.tyrkanych.entity.Report;
import java.util.List;

public interface ReportService {

    Report createReport(Long fromUserId,
            Long reportedUserId,
            String reason);

    List<Report> findPendingReports();

    List<Report> findByReportedUserId(Long userId);
}