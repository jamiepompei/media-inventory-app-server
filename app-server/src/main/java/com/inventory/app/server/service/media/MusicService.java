package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
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
public class MusicService {
    private IBaseDao<Music> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Music> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Music.class);
    }

    public List<Music> searchMusic(SearchMediaRequest searchMediaRequest){
        Optional<Predicate<Music>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        return searchPredicate.map(musicPredicate -> dao.findAll().stream()
                .filter(musicPredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Predicate<Music>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Music> predicate = music -> true; // Default Predicate
        if (searchMediaRequest.getCollectionTitle() != null && !searchMediaRequest.getCollectionTitle().isEmpty()) {
            predicate = predicate.and(music -> music.getCollectionTitle().equals(searchMediaRequest.getCollectionTitle()));
        }
        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(music -> music.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(music -> music.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(music -> music.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(ARTISTS) != null && !searchMediaRequest.getAdditionalAttributes().get(ARTISTS).toString().isEmpty()) {
            predicate = predicate.and(music -> music.getArtists().equals(searchMediaRequest.getAdditionalAttributes().get(ARTISTS)));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(SONG_LIST) != null && !searchMediaRequest.getAdditionalAttributes().get(SONG_LIST).toString().isEmpty()) {
            predicate = predicate.and(music -> music.getSongList().equals(searchMediaRequest.getAdditionalAttributes().get(SONG_LIST)));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR) != null) {
            predicate = predicate.and(music -> music.getReleaseYear().equals(searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR)));
        }
        return Optional.of(predicate);
    }

    public Optional<Music> getById(Long id, String username){
        return Optional.of(dao.findOne(id, username));
    }

    public Music create(Music music) {
       Optional<Music> existingMusic = getById(music.getId(), music.getCreatedBy());
       if (existingMusic.isPresent()) {
           throw new ResourceAlreadyExistsException("Cannot create music because movie already exists: " + music);
       }
       return dao.createOrUpdate(music);
    }

    public Music update(Music updatedMusic) {
        Optional<Music> existingMusic = getById(updatedMusic.getId(), updatedMusic.getCreatedBy());
        if (existingMusic.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update music because music does not exist: " + updatedMusic);
        }
        if (verifyIfMusicUpdated(existingMusic.get(), updatedMusic)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingMusic+ "Updated Book: " + updatedMusic);
        }
        return dao.createOrUpdate(updatedMusic);
    }

    public Music deleteById(Long id, String username){
        Optional<Music> music = getById(id, username);
        if (music.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete music because music does not exist.");
        }
        dao.deleteById(id, username);
        return music.get();
    }

    private boolean verifyIfMusicUpdated(Music existingMusic, Music updatedMusic) {
        return existingMusic.equals(updatedMusic);
    }
}
