package com.inventory.app.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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

    public List<T> createQuery(String fieldName, Object fieldValue) {
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

    @Override
    public T findOne(final long id, final String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);
        Root<T> root = query.from(clazz);

        query.select(root)
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("id"), id),
                        criteriaBuilder.equal(root.get("createdBy"), username)
                ));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public T findById(final long id) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);
//        Root<T> root = query.from(clazz);
//
//        query.select(root)
//                .where(criteriaBuilder.and(
//                        criteriaBuilder.equal(root.get("id"), id)
//                ));
       return entityManager.find(clazz, id);
       // return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public T createOrUpdate(final T entity) {
        entityManager.merge(entity);
        return entity;
    }

    @Override
    public void delete(final T entity) {
        entityManager.remove(entity);
    }

    @Override
    public void deleteById(final long entityId, final String username) {
        final T entity = findOne(entityId, username);
        delete(entity);
    }

    @Override
    public List<T> findAll(){
        return entityManager.createQuery("from " + clazz.getName()).getResultList();
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
