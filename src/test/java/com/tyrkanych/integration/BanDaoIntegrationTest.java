package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tyrkanych.dao.impl.BanDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Ban;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class BanDaoIntegrationTest extends BaseIntegrationTest {

    private final BanDaoImpl dao = new BanDaoImpl();
    private final UserDaoImpl userDao = new UserDaoImpl();

    @Test
    void testBan() {
        User user = userDao.save(new User("ban@mail.com", "1", "Ban", "male", LocalDate.now()));

        Ban ban = new Ban();
        ban.setUserId(user.getId());
        ban.setAdminId(1L);

        dao.save(ban);

        assertNotNull(ban.getId());
    }
}