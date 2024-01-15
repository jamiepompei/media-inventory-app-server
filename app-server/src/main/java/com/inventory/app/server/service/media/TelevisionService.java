//package com.inventory.app.server.service.media;
//
//import com.inventory.app.server.entity.TelevisionShow;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class TelevisionService {
//    private IExtendedDao<TelevisionShow, Long> dao;
//    @Autowired
//    public void setDao(IExtendedDao<TelevisionShow, Long> daoToSet) {
//        dao = daoToSet;
//        dao.setClazz(TelevisionShow.class);
//    }
//
//    List<TelevisionShow> finalAllByGenre(String genre){
//        return dao.findByAttributeContainsText("genre", genre);
//    }
//
//    List<TelevisionShow> findAllByTitle(String title) {
//        return dao.findByAttributeContainsText("title", title);
//    }
//}
