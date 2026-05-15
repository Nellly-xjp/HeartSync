package com.tyrkanych.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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