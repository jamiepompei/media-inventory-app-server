package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.*;

@Service
public class VideoGameService {
    private IBaseDao<Game> dao;

    public VideoGameService(IBaseDao<Game> dao) {
        this.dao = dao;
    }

    @Autowired
    public void setDao(IBaseDao<Game> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Game.class);
    }

    public List<Game> searchGames(SearchMediaRequest searchMediaRequest) {
        Optional<Predicate<Game>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        return searchPredicate.map(gamePredicate -> dao.findAll().stream()
                .filter(gamePredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Predicate<Game>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Game> predicate = game -> true; // Defaul Predicate
        if (searchMediaRequest.getCollectionTitle() != null && !searchMediaRequest.getCollectionTitle().isEmpty()) {
            predicate = predicate.and(game -> game.getCollectionTitle().equals(searchMediaRequest.getCollectionTitle()));
        }
        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(game -> game.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(game -> game.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(game -> game.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(CONSOLES.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(CONSOLES.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(game -> game.getConsoles().equals(searchMediaRequest.getAdditionalAttributes().get(CONSOLES.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(NUMBER_OF_PLAYERS.getJsonKey()) != null) {
            predicate = predicate.and(game -> game.getNumberOfPlayers().equals(searchMediaRequest.getAdditionalAttributes().get(NUMBER_OF_PLAYERS.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey()) != null) {
            predicate = predicate.and(game -> game.getReleaseYear().equals(searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey())));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(game -> game.getCreatedBy().equals(searchMediaRequest.getUsername()));
        }
        return Optional.of(predicate);
    }

    public Optional<Game> getById(Long id, String username) {
        Game game =  dao.findOne(id, username);
       return game == null ? Optional.empty() : Optional.of(game);
    }

    public Game create(Game game) {
        Optional<Game> existingGame = getById(game.getId(), game.getCreatedBy());
        if (existingGame.isPresent()) {
            throw new ResourceAlreadyExistsException("Cannot create game because games already exist: " + game);
        }
        return dao.createOrUpdate(game);
    }

    public Game update(Game updatedGame) {
        Optional<Game> existingGame = getById(updatedGame.getId(), updatedGame.getCreatedBy());

        if (existingGame.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update game because no game exists " + updatedGame);
        }
        if (verifyIfGameUpdate(existingGame.get(), updatedGame)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Game: " + existingGame + " Update Game: " + updatedGame);
        }
        return dao.createOrUpdate(updatedGame);
    }

    public Game deleteById(Long id, String username){
       Optional<Game> game = getById(id, username);
       if (game.isEmpty()) {
           throw new ResourceNotFoundException("Cannot delete game because game does not exist.");
       }
       dao.deleteById(id, username);
       return game.get();
    }

    private boolean verifyIfGameUpdate(Game existingGame, Game updatedGame) {
        return existingGame.equals(updatedGame);
    }
}
