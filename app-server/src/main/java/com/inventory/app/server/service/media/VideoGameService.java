package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.VideoGame;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.*;

@Service
public class VideoGameService {
    private IBaseDao<VideoGame> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<VideoGame> daoToSet) {
        dao = daoToSet;
        dao.setClazz(VideoGame.class);
    }

    public List<VideoGame> searchGames(SearchMediaRequest searchMediaRequest) {
        Optional<Predicate<VideoGame>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        return searchPredicate.map(gamePredicate -> dao.findAll().stream()
                .filter(gamePredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Predicate<VideoGame>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<VideoGame> predicate = game -> true; // Defaul Predicate
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
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(game -> game.getCreatedBy().equals(searchMediaRequest.getUsername()));
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
        return Optional.of(predicate);
    }

    public Optional<VideoGame> getById(Long id, String username) {
        VideoGame game =  dao.findOne(id, username);
       return game == null ? Optional.empty() : Optional.of(game);
    }

    public VideoGame create(VideoGame game) {
        Optional<VideoGame> existingGame = getById(game.getId(), game.getCreatedBy());
        if (existingGame.isPresent()) {
            throw new ResourceAlreadyExistsException("Cannot create game because games already exist: " + game);
        }
        return dao.createOrUpdate(game);
    }

    public VideoGame update(VideoGame updatedGame) {
        Optional<VideoGame> existingGame = getById(updatedGame.getId(), updatedGame.getCreatedBy());

        if (existingGame.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update game because no game exists " + updatedGame);
        }
        if (verifyIfGameUpdate(existingGame.get(), updatedGame)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Game: " + existingGame + " Update Game: " + updatedGame);
        }
        return dao.createOrUpdate(updatedGame);
    }

    public VideoGame deleteById(Long id, String username){
       Optional<VideoGame> game = getById(id, username);
       if (game.isEmpty()) {
           throw new ResourceNotFoundException("Cannot delete game because game does not exist.");
       }
       dao.deleteById(id, username);
       return game.get();
    }

    private boolean verifyIfGameUpdate(VideoGame existingGame, VideoGame updatedGame) {
        return existingGame.equals(updatedGame);
    }
}
