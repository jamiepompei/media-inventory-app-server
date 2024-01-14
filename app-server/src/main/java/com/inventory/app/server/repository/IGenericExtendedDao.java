package com.inventory.app.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface IGenericExtendedDao<T, ID extends Serializable> extends JpaRepository<T, ID> {
    void setClazz(Class< T > clazzToSet);

    public List<T> findByAttributeContainsText(String attributeName, String text);

    T findOne(final long id);

    List<T> findAll();

    T create(final T entity);

    T update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}
