package com.inventory.app.server.repository;


import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


import java.io.Serializable;

@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Qualifier(value = "genericDaoImpl")
public class BaseDaoImpl<T, ID extends Serializable> extends BaseDao<T, ID> implements IBaseDao<T, ID> {

    public BaseDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }
}
