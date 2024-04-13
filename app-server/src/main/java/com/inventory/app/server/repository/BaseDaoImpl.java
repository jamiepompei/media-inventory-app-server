package com.inventory.app.server.repository;


import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


import java.io.Serializable;

@Qualifier(value = "genericDaoImpl")
@Repository
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class BaseDaoImpl<T extends Serializable> extends BaseDao<T> {

    public BaseDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }
}
