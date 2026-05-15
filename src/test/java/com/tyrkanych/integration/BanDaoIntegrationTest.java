package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tyrkanych.dao.impl.BanDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Ban;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BanDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BanDaoImpl banDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void testBan() {
        User user = userDao.save(
                new User("ban@mail.com", "1", "Ban", "male", LocalDate.now()));

        Ban ban = new Ban();
        ban.setUserId(user.getId());
        ban.setAdminId(1L);
        banDao.save(ban);

        assertNotNull(ban.getId());
    }
}