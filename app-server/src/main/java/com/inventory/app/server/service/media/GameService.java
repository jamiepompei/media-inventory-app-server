package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private IBaseDao<Game> dao;

    public GameService(IBaseDao<Game> dao) {
        this.dao = dao;
    }

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
        if (gameAlreadyExists(game)) {
            throw new ResourceAlreadyExistsException("Cannot create game because games already exist: " + game);
        }
        Game gameToSave = cloneGame(game);
        gameToSave.setId(null);
        gameToSave.setVersion(1);
        return dao.createOrUpdate(gameToSave);
    }

    public Game update(Game updatedGame) {
        if (!gameAlreadyExists(updatedGame)) {
            throw new ResourceNotFoundException("Cannot update game because game does not exist: " + updatedGame);
        }
        Game existingGame = getGameById(updatedGame.getId());
        if (verifyIfGameUpdate(existingGame, updatedGame)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Game: " + existingGame + " Update Game: " + updatedGame);
        }
        updatedGame = cloneGame(existingGame, updatedGame);
        updatedGame.setVersion(existingGame.getVersion() + 1);
        return dao.createOrUpdate(updatedGame);
    }

    public Game deleteById(Long id){
       Game game = getGameById(id);
       if (game == null) {
           throw new ResourceNotFoundException("Cannot delete game because game does not exist.");
       }
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

    private boolean verifyIfGameUpdate(Game existingGame, Game updatedGame) {
        return existingGame.equals(updatedGame);
    }
}
