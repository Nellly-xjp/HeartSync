package com.tyrkanych.repository;

import com.tyrkanych.dao.BaseDao;
import java.lang.reflect.ParameterizedType;

public class GenericRepository<T, ID> {

    protected final BaseDao<T, ID> dao;
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public GenericRepository(BaseDao<T, ID> dao) {
        this.dao = dao;
        this.entityClass = (Class<T>) ((ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T save(T entity) {
        return dao.save(entity);
    }

    public T findById(ID id) {
        return dao.findById(id).orElse(null);
    }

    public void delete(ID id) {
        dao.deleteById(id);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}