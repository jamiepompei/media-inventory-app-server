package com.inventory.app.server.repository;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
@Repository
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public interface IBaseDao<T, ID extends Serializable>  {
    void setClazz(Class< T > clazzToSet);

    Class<T> getClazz();

    List<T> findByField(String field, Object value, Class<T> clazz);

    T findOne(final long id);

    List<T> findAll();

    T create(final T entity);

    T update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}
