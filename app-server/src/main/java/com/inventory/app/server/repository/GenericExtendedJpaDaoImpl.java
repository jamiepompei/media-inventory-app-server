package com.inventory.app.server.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;


import java.io.Serializable;
import java.util.List;

public class GenericExtendedJpaDaoImpl<T, ID extends Serializable> extends AbstractJpaDao<T, ID> implements IGenericExtendedDao<T, ID> {

    public GenericExtendedJpaDaoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
}
