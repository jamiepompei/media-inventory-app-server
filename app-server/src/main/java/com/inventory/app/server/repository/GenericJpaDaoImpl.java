package com.inventory.app.server.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Qualifier(value = "genericDaoImpl")
public class GenericJpaDaoImpl<T, ID extends Serializable> extends AbstractJpaDao<T, ID> implements IGenericDao<T, ID> {

    public GenericJpaDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }
}
