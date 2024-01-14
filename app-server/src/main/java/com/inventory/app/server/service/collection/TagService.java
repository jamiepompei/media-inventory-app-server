package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.collection.Tag;
import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {
    private IGenericExtendedDao<Tag, Long> dao;

    @Autowired
    public void setDao(IGenericExtendedDao<Tag, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Tag.class);
    }

    Optional<Tag> findByTag(String tag) {
        return dao.findByAttributeContainsText("tag", tag).stream().findFirst();
    }
}
