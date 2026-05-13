package com.tyrkanych.dao;

import com.tyrkanych.entity.Report;
import java.util.List;

public interface ReportDao extends BaseDao<Report, Long> {

    List<Report> findByReportedUserId(Long reportedUserId);

    List<Report> findByStatus(String status);

    List<Report> findPendingReports();
}