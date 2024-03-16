package com.inventory.app.server.repository;

import java.io.Serializable;
import java.util.List;

public interface IBaseDao<T extends Serializable> {
    void setClazz(Class< T > clazzToSet);

    Class<T> getClazz();

    List<T> findByField(String field, Object value);

    T findOneByField(String field, Object value);

    T findOne(final long id);

    List<T> findAll();

    T createOrUpdate(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}
