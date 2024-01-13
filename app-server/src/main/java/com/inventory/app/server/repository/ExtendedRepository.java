package com.inventory.app.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ExtendedRepository <T, ID extends Serializable> extends JpaRepository<T, ID> {

    public List<T> findByAttributeContainsText(String attributeName, String text);
}
