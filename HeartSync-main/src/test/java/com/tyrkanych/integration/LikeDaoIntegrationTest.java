// ===== LikeDaoIntegrationTest.java =====
package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.LikeDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Like;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LikeDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private LikeDaoImpl likeDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void testLikeLifecycle() {
        User u1 = userDao.save(
                new User("a@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("b@mail.com", "1", "B", "female", LocalDate.now()));

        Like like = new Like(u1.getId(), u2.getId());
        likeDao.save(like);

        assertTrue(likeDao.existsLike(u1.getId(), u2.getId()));

        likeDao.deleteLike(u1.getId(), u2.getId());
        assertFalse(likeDao.existsLike(u1.getId(), u2.getId()));
    }
}