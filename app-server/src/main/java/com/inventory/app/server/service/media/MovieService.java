//package com.inventory.app.server.service.media;
//
//import com.inventory.app.server.entity.Movie;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class MovieService {
//    private IExtendedDao<Movie, Long> dao;
//
//    @Autowired
//    public void setDao(IExtendedDao<Movie, Long> daoToSet) {
//        dao = daoToSet;
//        dao.setClazz(Movie.class);
//    }
//
//    List<Movie> findAllByGenre(String genre){
//        return dao.findByAttributeContainsText("genre", genre);
//    }
//
//    List<Movie> findAllByTitle(String title){
//        return dao.findByAttributeContainsText("title", title);
//    }
//}
