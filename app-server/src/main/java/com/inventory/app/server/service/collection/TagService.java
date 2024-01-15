package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.Tag;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    private IBaseDao<Tag, Long> dao;

    @Autowired
    public void setDao(IBaseDao<Tag, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Tag.class);
    }

}
