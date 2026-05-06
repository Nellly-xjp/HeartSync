package com.tyrkanych.identity;

import java.util.HashMap;
import java.util.Map;

public class IdentityMap<ID, T> {

    private final Map<ID, T> cache = new HashMap<>();

    public void put(ID id, T entity) {
        cache.put(id, entity);
    }

    public T get(ID id) {
        return cache.get(id);
    }

    public boolean contains(ID id) {
        return cache.containsKey(id);
    }

    public void clear() {
        cache.clear();
    }
}