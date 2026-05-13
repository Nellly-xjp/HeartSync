// ===== UserServiceIntegrationTest.java =====
package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dto.UserDto;
import com.tyrkanych.dto.UserRegistrationDto;
import com.tyrkanych.service.UserService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    private UserRegistrationDto buildDto(String email) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail(email);
        dto.setPassword("password123");
        dto.setName("Тест Юзер");
        dto.setGender("female");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        dto.setCity("Київ");
        return dto;
    }

    @Test
    void testRegister() {
        UserDto result = userService.register(buildDto("new@test.com"));

        assertNotNull(result.getId());
        assertEquals("new@test.com", result.getEmail());
        assertTrue(result.getAge() > 0);
    }

    @Test
    void testDuplicateEmailThrows() {
        userService.register(buildDto("dup@test.com"));

        assertThrows(IllegalArgumentException.class,
                () -> userService.register(buildDto("dup@test.com")));
    }

    @Test
    void testFindByEmail() {
        userService.register(buildDto("find@test.com"));

        assertTrue(userService.findByEmail("find@test.com").isPresent());
    }

    @Test
    void testFindAll() {
        userService.register(buildDto("all1@test.com"));
        userService.register(buildDto("all2@test.com"));

        assertEquals(2, userService.findAll().size());
    }
}