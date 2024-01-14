package com.inventory.app.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;


import java.io.Serializable;
import java.util.List;

public abstract class AbstractJpaDao<T, ID extends Serializable> extends SimpleJpaRepository< T, ID > implements IGenericExtendedDao< T, ID> {
    private Class<T> clazz;

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    public AbstractJpaDao(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public final void setClazz(final Class<T> clazzToSet){
        this.clazz = clazzToSet;
    }

    @Transactional
    public List<T> findByAttributeContainsText(String attributeName, String text) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getDomainClass());
        Root<T> root = criteriaQuery.from(getDomainClass());
        criteriaQuery
                .select(root)
                .where(criteriaBuilder
                        .like(root.get(attributeName), "%" + text + "%"));
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
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

}
