package com.tyrkanych.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Допоміжний клас для передачі Spring ApplicationContext у FXML controller factory. Дозволяє
 * FXMLLoader отримувати контролери зі Spring IoC — без getInstance().
 */
@Component
public class SpringFxmlContext {

    private static ApplicationContext context;

    public SpringFxmlContext(ApplicationContext ctx) {
        SpringFxmlContext.context = ctx;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}