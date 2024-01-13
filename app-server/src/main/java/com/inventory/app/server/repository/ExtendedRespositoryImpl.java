package com.inventory.app.server.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;


import java.io.Serializable;
import java.util.List;

public class ExtendedRespositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {
    private EntityManager entityManager;

    public ExtendedRespositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super((JpaEntityInformation<T, ?>) entityInformation, (jakarta.persistence.EntityManager) entityManager);
        this.entityManager = entityManager;
    }

    @Override
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
}
