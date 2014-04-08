package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;

public class ProjectDaoImpl<T extends ProjectEntity<ID>, ID> implements ProjectDao<T, ID> {

    protected Class<T> entityClass;
    @PersistenceContext
    protected EntityManager entityManager;

    public T find(ID id) {
        return entityManager.find(entityClass, id);
    }

    public List<T> findAll() {
        return entityManager.createQuery("select e from " + entityClass.getName() + " e ").getResultList();
    }

    public void add(T entity) {
        entityManager.persist(entity);
    }

    public T update(T entity) {
        return entityManager.merge(entity);

    }

    public void delete(T entity) {
        entityManager.remove(entity);
    }

    @Override
    @Transactional
    public void flush() {
        entityManager.flush();

    }
}


