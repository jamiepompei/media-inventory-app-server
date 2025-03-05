package com.inventory.app.server.repository;

import java.io.Serializable;
import java.util.List;

public interface IBaseDao<T extends Serializable> {

    void setClazz(final Class< T > clazzToSet);

    Class<T> getClazz();

    T findOne(final long id, final String username);

    T findById(final long id);

    List<T> findAll();

    T createOrUpdate(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId, final String username);
}
