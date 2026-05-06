package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tyrkanych.dao.impl.ReportDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Report;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ReportDaoIntegrationTest extends BaseIntegrationTest {

    private final ReportDaoImpl dao = new ReportDaoImpl();
    private final UserDaoImpl userDao = new UserDaoImpl();

    @Test
    void testReport() {
        User u1 = userDao.save(new User("r1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(new User("r2@mail.com", "1", "B", "female", LocalDate.now()));

        Report r = new Report();
        r.setFromUserId(u1.getId());
        r.setReportedUserId(u2.getId());
        r.setReason("spam");
        r.setStatus("OPEN");

        dao.save(r);

        assertNotNull(r.getId());
    }
}