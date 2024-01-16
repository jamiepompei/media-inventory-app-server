package com.inventory.app.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;


import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

public abstract class BaseDao<T, ID extends Serializable>  implements IBaseDao< T, ID > {
    private Class<T> clazz;

    @PersistenceContext(unitName = "entityManagerFactory")
    private final EntityManager entityManager;

    public BaseDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public List<T> findByField(String field, Object value) {
        return createQuery(field, value);
    }

    private List<T> createQuery(String fieldName, Object fieldValue) {
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(getClazz(), entityManager);
        String entityName = entityInformation.getEntityName();
        Class<T> entityType = entityInformation.getJavaType();

        String queryString = String.format("FROM %s WHERE %s = :value", entityName, fieldName);
        TypedQuery<T> query = entityManager.createQuery(queryString, entityType);
        return query.setParameter("value", fieldValue).getResultList();
    }

    public final void setClazz(final Class<T> clazzToSet){
        this.clazz = clazzToSet;
    }

    public T findOne(final long id) {
        return entityManager.find(clazz, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll(){
        return entityManager.createQuery("from " + clazz.getName()).getResultList();
    }

    public T create(final T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T update(final T entity) {
        return entityManager.merge(entity);
    }

    public void delete(final T entity) {
        entityManager.remove(entity);
    }

    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        delete(entity);
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
