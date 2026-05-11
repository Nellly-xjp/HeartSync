package com.tyrkanych.repository;

import com.tyrkanych.dao.BaseDao;

/**
 * Базовий репозиторій — делегує виклики до відповідного DAO.
 * <p>
 * Виправлення: прибрано рефлексію getActualTypeArguments(), яка падала при використанні
 * конструктора з параметром замість прямого наслідування з типом.
 */
public class GenericRepository<T, ID> {

    protected final BaseDao<T, ID> dao;

    public GenericRepository(BaseDao<T, ID> dao) {
        this.dao = dao;
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
}