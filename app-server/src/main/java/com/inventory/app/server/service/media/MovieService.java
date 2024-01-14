package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private IGenericExtendedDao<Movie, Long> dao;

    @Autowired
    public void setDao(IGenericExtendedDao<Movie, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Movie.class);
    }

    List<Movie> findAllByGenre(String genre){
        return dao.findByAttributeContainsText("genre", genre);
    }

    List<Movie> findAllByTitle(String title){
        return dao.findByAttributeContainsText("title", title);
    }
}
