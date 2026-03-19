package com.inventory.app.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
@Slf4j
public abstract class BaseDao<T extends Serializable>  implements IBaseDao< T >{
    private Class<T> clazz;

    @PersistenceContext(unitName = "entityManagerFactory")
    private final EntityManager entityManager;

    public BaseDao(EntityManager entityManager) {
        this.entityManager = entityManager;
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
       return entityManager.find(clazz, id);
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
        String className = clazz.getName();
        TypedQuery<T> typedQuery = entityManager.createQuery("from " + className, clazz);
        List<T> resultList = typedQuery.getResultList();
        log.info("Retrieved {} entities of type {} from the database.", resultList.size(), className);
        return resultList;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
