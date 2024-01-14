package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicService {
    private IGenericExtendedDao<Music, Long> dao;

    @Autowired
    public void setDao(IGenericExtendedDao<Music, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Music.class);
    }

    public List<Music> findAllByArtist(String artist){
       return dao.findByAttributeContainsText("aritst", artist);
    }

    public List<Music> findAllByGenre(String genre){
        return dao.findByAttributeContainsText("genre", genre);
    }

    public List<Music> finalAllBySong(String song){
        return dao.findByAttributeContainsText("song", song);
    }
}
