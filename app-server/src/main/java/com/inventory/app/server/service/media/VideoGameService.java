package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.VideoGame;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.mapper.VideoGameMapper;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Transactional
@Slf4j
public class VideoGameService implements BaseService<VideoGame>{
    private IBaseDao<VideoGame> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<VideoGame> daoToSet) {
        dao = daoToSet;
        dao.setClazz(VideoGame.class);
    }

    public List<MediaResponse> search(SearchMediaRequest searchMediaRequest) {
        log.info("Initiating search for video games with search criteria: {}", searchMediaRequest);
        Optional<Predicate<VideoGame>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        List<MediaResponse> mediaResponses = searchPredicate.map(gamePredicate -> dao.findAll().stream()
                .filter(gamePredicate)
                .map(VideoGameMapper.INSTANCE::mapVideoGameToMediaResponse)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        if (mediaResponses.isEmpty()) {
            log.info("No video games found matching search criteria: " + searchMediaRequest);
        } else {
            log.info("Search completed successfully. Number of video games found: " + mediaResponses.size());
        }
        return mediaResponses;
    }

    private Optional<Predicate<VideoGame>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<VideoGame> predicate = game -> true; // Defaul Predicate

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
        log.info("Search predicate: {} built successfully for search criteria: {}", predicate, searchMediaRequest);
        return Optional.of(predicate);
    }

    public Optional<VideoGame> getById(Long id, String username) {
        log.info("Initiating retrieval of video game with ID: {} for user: {}", id, username);
        VideoGame game =  dao.findOne(id, username);
        if (game == null) {
            log.warn("Book with ID: {} not found for user: {}", id, username);
            return Optional.empty();
        } else {
            log.info("Book with ID: {} retrieved successfully for user: {}", id, username);
            return Optional.of(game);
        }
    }

    public MediaResponse create(UpdateCreateMediaRequest updateCreateMediaRequest) {
        VideoGame game = VideoGameMapper.INSTANCE.mapMediaRequestToVideoGame(updateCreateMediaRequest);
        Optional<VideoGame> existingGame = getById(game.getId(), game.getCreatedBy());
        if (existingGame.isPresent()) {
            log.warn("Attempting to create a book with an id that already exists. Video game: " + game);
            throw new ResourceAlreadyExistsException("Cannot create game because games already exist: " + game);
        }
        log.info("Initiating video game POST request. Video game to be created: {}", game);
        MediaResponse response = VideoGameMapper.INSTANCE.mapVideoGameToMediaResponse(dao.createOrUpdate(game));
        log.info("Video game created successfully with ID: {}", game.getId());
        return response;
    }

    public MediaResponse update(UpdateCreateMediaRequest updateCreateMediaRequest) {
        VideoGame updatedGame = VideoGameMapper.INSTANCE.mapMediaRequestToVideoGame(updateCreateMediaRequest);
        Optional<VideoGame> existingGame = getById(updatedGame.getId(), updatedGame.getCreatedBy());

        if (existingGame.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update game because no game exists " + updatedGame);
        }
        if (verifyIfGameUpdate(existingGame.get(), updatedGame)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Game: " + existingGame + " Update Game: " + updatedGame);
        }
        log.info("Initiating video game PUT request for game with ID: {}. Updated game details: {}", updatedGame.getId(), updatedGame);
        MediaResponse response = VideoGameMapper.INSTANCE.mapVideoGameToMediaResponse(dao.createOrUpdate(updatedGame));
        log.info("Video game with ID: {} updated successfully.", updatedGame.getId());
        return response;
    }

    public Long deleteById(Long id, String username){
        log.info("Initiating book DELETE request for video game with ID: {} by user: {}", id, username);
       Optional<VideoGame> game = getById(id, username);
       if (game.isEmpty()) {
           log.warn("Attempting to delete a video game that does not exist. Book ID: {}, User: {}", id, username);
           throw new ResourceNotFoundException("Cannot delete game because game does not exist.");
       }
       dao.deleteById(id, username);
        log.info("Video game with ID: {} deleted successfully by user: {}", id, username);
       return game.get().getId();
    }

    private boolean verifyIfGameUpdate(VideoGame existingGame, VideoGame updatedGame) {
        return existingGame.equals(updatedGame);
    }
}
