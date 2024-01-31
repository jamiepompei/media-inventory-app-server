package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.utility.RestPreConditions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private IBaseDao<Game> dao;

    @Autowired
    public void setDao(IBaseDao<Game> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Game.class);
    }

    public List<Game> getAllGamesByNumberOfPlayers(Integer numberOfPlayers){
        return dao.findByField("number_of_players", String.valueOf(numberOfPlayers));
    }

    public List<Game> getAllGamesByConsole(List<String> console){
        return  dao.findByField("consoles", console);
    }

    public List<Game> getAllGamesByTitle(String title){
        return dao.findByField("title", title);
    }

    public List<Game> getAllGamesByCollectionTitle(String collectionTitle) {
        return dao.findByField("collection_name", collectionTitle);
    }

    public List<Game> getAllGamesByGenre(String genre) {
        return dao.findByField("genre", genre);
    }

    public List<Game> getAllGames() {
        return dao.findAll();
    }

    public Game getGameById(Long id) {
        return dao.findOne(id);
    }

    public Game create(Game game) {
        // validations before performing create
        RestPreConditions.checkAlreadyExists(gameAlreadyExists(game), game);

        Game gameToSave = cloneGame(game);
        gameToSave.setId(null);
        gameToSave.setVersion(1);

        return dao.createOrUpdate(gameToSave);
    }

    public Game update(Game updatedGame) {
        //validations before performing the update
        Game existingGame = RestPreConditions.checkFound(getGameById(updatedGame.getId()));
        RestPreConditions.checkEquals(existingGame, updatedGame);

        updatedGame = cloneGame(existingGame, updatedGame);
        updatedGame.setVersion(existingGame.getVersion() + 1);

        return dao.createOrUpdate(updatedGame);
    }

    public Game deleteById(Long id){
       Game game = RestPreConditions.checkFound(getGameById(id));
        dao.deleteById(id);
        return game;
    }

    private Game cloneGame(Game game) {
        Game clonedGame = new Game();
        BeanUtils.copyProperties(game, clonedGame);
        return clonedGame;
    }

    private Game cloneGame(Game existingGame, Game updatedGame) {
        BeanUtils.copyProperties(updatedGame, existingGame);
        return existingGame;
    }

    private boolean gameAlreadyExists(Game game) {
        return getAllGamesByTitle(game.getTitle())
                .stream()
                .anyMatch(g -> game.getTitle().equals(g.getTitle()));
    }
}
