package com.inventory.app.server.repository;

import java.io.Serializable;
import java.util.List;

public interface IBaseDao<T extends Serializable> {
    void setClazz(final Class< T > clazzToSet);

    Class<T> getClazz();

    List<T> findByField(final String field, final Object value, final String username);

    T findOneByField(final String field, final Object value, final String username);

    T findOne(final long id, final String username);

    List<T> findAllByUsername(final String username);

    List<T> findAll();

    T createOrUpdate(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId, final String username);
}
