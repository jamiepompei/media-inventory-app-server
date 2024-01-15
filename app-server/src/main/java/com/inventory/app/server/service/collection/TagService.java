package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.Tag;
import com.inventory.app.server.repository.IGenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {
    private IGenericDao<Tag, Long> dao;

    @Autowired
    public void setDao(IGenericDao<Tag, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Tag.class);
    }

}
