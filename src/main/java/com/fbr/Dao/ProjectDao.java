package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public interface ProjectDao<T, ID> {

    public T find(ID id);

    public List<T> findAll();

    public void add(T entity);

    public T update(T entity);

    public void delete(T entity);

    public void flush();
}
