package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
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

    public List<Game> getAllGamesByNumberOfPlayers(Integer numberOfPlayers, String username){
        List<Game> gameList = dao.findByField("number_of_players", String.valueOf(numberOfPlayers), username);
        if (gameList.isEmpty()) {
            throw new ResourceNotFoundException("No games found with number of players: " + numberOfPlayers);
        }
        return gameList;
    }

    public List<Game> getAllGamesByConsole(List<String> console, String username){
        List<Game> gameList = dao.findByField("consoles", console, username);
        if (gameList.isEmpty()) {
            throw new ResourceNotFoundException("No games found for consoles " + console );
        }
        return gameList;
    }

    public List<Game> getAllGamesByTitle(String title, String username){
        List<Game> gameList = dao.findByField("title", title, username);
        if (gameList.isEmpty()) {
            throw new ResourceNotFoundException("No game results for title " + title);
        }
        return gameList;
    }

    public List<Game> getAllGamesByCollectionTitle(String collectionTitle, String username) {
        List<Game> gameList = dao.findByField("collection_name", collectionTitle, username);
        if (gameList.isEmpty()) {
            throw new ResourceNotFoundException("No game results found for collection title " + collectionTitle);
        }
        return gameList;
    }

    public List<Game> getAllGamesByGenre(String genre, String username) {
        List<Game> gameList = dao.findByField("genre", genre, username);
        if (gameList.isEmpty()) {
            throw new ResourceNotFoundException("No fame results found for genre " + genre);
        }
        return gameList;
    }

    public List<Game> getAllByUsername(String username) {
        List<Game> gameList = dao.findAllByUsername(username);
        if (gameList.isEmpty()) {
            throw new ResourceNotFoundException("No game data exists.");
        }
        return gameList;
    }

    public Game getById(Long id, String username) {
        try {
            return dao.findOne(id, username);
        } catch (Exception e) {
            if(e.getClass().isInstance(EntityNotFoundException.class)) {
                throw new ResourceNotFoundException("No game exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public Game create(Game game, String username) {
        if (gameAlreadyExists(game, username)) {
            throw new ResourceAlreadyExistsException("Cannot create game because games already exist: " + game);
        }
        Game gameToSave = cloneGame(game);
        gameToSave.setId(null);
        gameToSave.setVersion(1);
        return dao.createOrUpdate(gameToSave);
    }

    public Game update(Game updatedGame, String username) {
        if (!gameAlreadyExists(updatedGame, username)) {
            throw new ResourceNotFoundException("Cannot update game because game does not exist: " + updatedGame);
        }
        Game existingGame = getById(updatedGame.getId(), username);
        if (verifyIfGameUpdate(existingGame, updatedGame)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Game: " + existingGame + " Update Game: " + updatedGame);
        }
        updatedGame = cloneGame(updatedGame);
        updatedGame.setId(existingGame.getId());
        updatedGame.setVersion(existingGame.getVersion() + 1);
        return dao.createOrUpdate(updatedGame);
    }

    public Game deleteById(Long id, String username){
       Game game = getById(id, username);
       if (game == null) {
           throw new ResourceNotFoundException("Cannot delete game because game does not exist.");
       }
       dao.deleteById(id, username);
       return game;
    }

    private Game cloneGame(Game game) {
        Game clonedGame = new Game();
        BeanUtils.copyProperties(game, clonedGame);
        return clonedGame;
    }

    private boolean gameAlreadyExists(Game game, String username) {
        return getAllGamesByTitle(game.getTitle(), username)
                .stream()
                .anyMatch(g -> game.getTitle().equals(g.getTitle()));
    }

    private boolean verifyIfGameUpdate(Game existingGame, Game updatedGame) {
        return existingGame.equals(updatedGame);
    }
}
