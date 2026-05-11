package com.tyrkanych.service.impl;

import com.tyrkanych.dao.ReportDao;
import com.tyrkanych.entity.Report;
import com.tyrkanych.service.ReportService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;

    @Autowired
    public ReportServiceImpl(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    @Override
    public Report createReport(Long fromUserId, Long reportedUserId, String reason) {
        Report report = new Report();
        report.setFromUserId(fromUserId);
        report.setReportedUserId(reportedUserId);
        report.setReason(reason);
        report.setStatus("pending");

        return reportDao.save(report);
    }

    @Override
    public List<Report> findPendingReports() {
        return reportDao.findPendingReports();
    }

    @Override
    public List<Report> findByReportedUserId(Long userId) {
        return reportDao.findByReportedUserId(userId);
    }
}