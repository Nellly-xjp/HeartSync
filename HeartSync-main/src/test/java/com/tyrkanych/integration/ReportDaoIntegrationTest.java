// ===== ReportDaoIntegrationTest.java =====
package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tyrkanych.dao.impl.ReportDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Report;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ReportDaoImpl reportDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void testCreateReport() {
        User u1 = userDao.save(
                new User("r1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("r2@mail.com", "1", "B", "female", LocalDate.now()));

        Report r = new Report();
        r.setFromUserId(u1.getId());
        r.setReportedUserId(u2.getId());
        r.setReason("spam");
        r.setStatus("pending");
        reportDao.save(r);

        assertNotNull(r.getId());
    }

    @Test
    void testFindPendingReports() {
        User u1 = userDao.save(
                new User("r3@mail.com", "1", "C", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("r4@mail.com", "1", "D", "female", LocalDate.now()));

        Report r = new Report();
        r.setFromUserId(u1.getId());
        r.setReportedUserId(u2.getId());
        r.setReason("inappropriate");
        r.setStatus("pending");
        reportDao.save(r);

        List<Report> pending = reportDao.findPendingReports();
        assertFalse(pending.isEmpty());
    }
}