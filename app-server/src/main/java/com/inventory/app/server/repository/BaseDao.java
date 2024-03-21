package com.inventory.app.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

import java.io.Serializable;
import java.util.List;

public abstract class BaseDao<T extends Serializable>  implements IBaseDao< T >{
    private Class<T> clazz;

    @PersistenceContext(unitName = "entityManagerFactory")
    private final EntityManager entityManager;

    public BaseDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<T> findByField(String field, Object value, String username) {
        return createQuery(field, value, username);
    }

    @Override
    public T findOneByField(String field, Object value, String username) {
        return findByField(field, value, username).stream().findFirst().get();
    }

    private List<T> createQuery(String fieldName, Object fieldValue, String username) {
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(getClazz(), entityManager);
        String entityName = entityInformation.getEntityName();
        Class<T> entityType = entityInformation.getJavaType();

        String queryString = String.format("FROM %s WHERE %s = :value AND createdBy = :username", entityName, fieldName);
        TypedQuery<T> query = entityManager.createQuery(queryString, entityType);
        query.setParameter("username", username);
        query.setParameter("value", fieldValue);
        return query.getResultList();
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

    public T createOrUpdate(final T entity) {
        entityManager.merge(entity);
        return entity;
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
