package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelevisionService {
    private IGenericExtendedDao<TelevisionShow, Long> dao;
    @Autowired
    public void setDao(IGenericExtendedDao<TelevisionShow, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(TelevisionShow.class);
    }

    List<TelevisionShow> finalAllByGenre(String genre){
        return dao.findByAttributeContainsText("genre", genre);
    }

    List<TelevisionShow> findAllByTitle(String title) {
        return dao.findByAttributeContainsText("title", title);
    }
}
