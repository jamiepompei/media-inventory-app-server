package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.mapper.MusicMapper;
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
public class MusicService implements BaseService<Music> {
    private IBaseDao<Music> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl")  IBaseDao<Music> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Music.class);
    }

    public List<MediaResponse> search(SearchMediaRequest searchMediaRequest){
        log.info("Initiating search for music with search criteria: {}", searchMediaRequest);
        Optional<Predicate<Music>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        List<MediaResponse> mediaResponses = searchPredicate.map(musicPredicate -> dao.findAll().stream()
                .filter(musicPredicate)
                .map(MusicMapper.INSTANCE::mapMusicToMediaResponse)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        if (mediaResponses.isEmpty()) {
            log.info("No music found matching search criteria: " + searchMediaRequest);
        } else {
            log.info("Search completed successfully. Number of music found: " + mediaResponses.size());
        }
        return mediaResponses;
    }

    private Optional<Predicate<Music>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Music> predicate = music -> true; // Default Predicate

        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(music -> music.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(music -> music.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(music -> music.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(music -> music.getCreatedBy().equals(searchMediaRequest.getUsername()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(ARTISTS.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(ARTISTS.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(music -> music.getArtists().equals(searchMediaRequest.getAdditionalAttributes().get(ARTISTS.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(SONG_LIST.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(SONG_LIST.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(music -> music.getSongList().equals(searchMediaRequest.getAdditionalAttributes().get(SONG_LIST.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey()) != null) {
            predicate = predicate.and(music -> music.getReleaseYear().equals(searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey())));
        }
        log.info("Search predicate: {} built successfully for search criteria : {}", predicate, searchMediaRequest);
        return Optional.of(predicate);
    }

    public Optional<Music> getById(Long id, String username){
        log.info("Initiating retrieval of music with id: {} for user: {}", id, username);
        Music music = dao.findOne(id, username);
        if (music == null){
            log.warn("Music with id: {} not found for user: {}", id, username);
            return Optional.empty();
        } else {
            log.info("Music with ID: {} retrieved successfully for user: {}", id, username);
            return Optional.of(music);
        }
    }

    public MediaResponse create(UpdateCreateMediaRequest updateCreateMediaRequest) {
        Music music = MusicMapper.INSTANCE.mapMediaRequestToMusic(updateCreateMediaRequest);
       Optional<Music> existingMusic = getById(music.getId(), music.getCreatedBy());
       if (existingMusic.isPresent()) {
           log.warn("Attempting to create music with an ID that already exists. Music: {}", music);
           throw new ResourceAlreadyExistsException("Cannot create music because movie already exists: " + music);
       }
       log.info("Initiating music POST request. Music to be created: {}", music);
       MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponse(dao.createOrUpdate(music));
       log.info("Music created successfully with ID: {}", response.getId());
       return response;
    }

    public MediaResponse update(UpdateCreateMediaRequest updatedMusic) {
        Music music = MusicMapper.INSTANCE.mapMediaRequestToMusic(updatedMusic);
        Optional<Music> existingMusic = getById(music.getId(), music.getCreatedBy());
        if (existingMusic.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update music because music does not exist: " + updatedMusic);
        }
        if (verifyIfMusicUpdated(existingMusic.get(), music)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingMusic+ "Updated Book: " + updatedMusic);
        }
        log.info("Initiating music PUT request. Music to be updated: {}", music);
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponse(dao.createOrUpdate(music));
        log.info("Music updated successfully with ID: {}", response.getId());
        return response;
    }

    public Long deleteById(Long id, String username){
        log.info("Initiating music DELETE request for music with ID: {} by user: {}", id, username);
        Optional<Music> music = getById(id, username);
        if (music.isEmpty()) {
            log.warn("Attempting to delete a book that does not exist. Music ID:{}, User: {}", id, username);
            throw new ResourceNotFoundException("Cannot delete music because music does not exist.");
        }
        dao.deleteById(id, username);
        log.info("Music with ID: {} deleted successfully for user: {}", id, username);
        return music.get().getId();
    }

    private boolean verifyIfMusicUpdated(Music existingMusic, Music updatedMusic) {
        return existingMusic.equals(updatedMusic);
    }
}
