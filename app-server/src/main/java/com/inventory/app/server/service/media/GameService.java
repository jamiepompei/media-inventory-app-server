//package com.inventory.app.server.service.media;
//
//import com.inventory.app.server.entity.media.Game;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class GameService {
//    private IExtendedDao<Game, Long> dao;
//
//    @Autowired
//    public void setDao(IExtendedDao<Game, Long> daoToSet) {
//        dao = daoToSet;
//        dao.setClazz(Game.class);
//    }
//
//    List<Game> findAllByNumberOfPlayers(Integer numberOfPlayers){
//        return dao.findByAttributeContainsText("number_of_players", String.valueOf(numberOfPlayers));
//    }
//
//    List<Game> findByConsole(String console){
//        return  dao.findByAttributeContainsText("console", console);
//    }
//
//    List<Game> findByTitle(String title){
//        return dao.findByAttributeContainsText("title", title);
//    }
//}
