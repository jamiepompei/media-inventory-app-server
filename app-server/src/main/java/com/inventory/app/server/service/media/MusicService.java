package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MusicService {
    private IBaseDao<Music> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Music> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Music.class);
    }

    public List<Music> getAllMusicByArtist(List<String> artist, String username){
       List<Music> musicList = dao.findByField("artist", artist, username);
       if (musicList.isEmpty()) {
           throw new ResourceNotFoundException("No music found by artist(s) " + artist);
       }
        return musicList;
    }

    public List<Music> getAllMusicByGenre(String genre, String username){
        List<Music> musicList = dao.findByField("genre", genre, username);
        if (musicList.isEmpty()) {
            throw new ResourceNotFoundException("No music found with genre " + genre);
        }
        return musicList;
    }

    public List<Music> getAllMusicByCollectionTitle(String collectionTitle, String username){
        List<Music> musicList = dao.findByField("collection_name", collectionTitle, username);
        if (musicList.isEmpty()) {
            throw new ResourceNotFoundException("No music found with collection title " + collectionTitle);
        }
        return musicList;
    }

    public List<Music> getAllByUsername(String username) {
        List<Music> musicList = dao.findAllByUsername(username);
        if (musicList.isEmpty()) {
            throw new ResourceNotFoundException("No music data exists.");
        }
        return musicList;
    }

    public Music getById(Long id, String username){
        try {
            return dao.findOne(id, username);
        } catch (Exception e) {
            if (e.getClass().isInstance(EntityNotFoundException.class)) {
                throw new ResourceNotFoundException("No music exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public Music create(Music music, String username) {
       if (musicAlreadyExists(music, username)) {
           throw new ResourceAlreadyExistsException("Cannot create music because music already exists: " + music);
        }
        Music musicToSave = cloneMusic(music);
        musicToSave.setId(null);
        musicToSave.setVersion(1);
        return dao.createOrUpdate(musicToSave);
    }

    public Music update(Music updatedMusic, String username) {
        if (!musicAlreadyExists(updatedMusic, username)) {
            throw new ResourceNotFoundException("Cannot update music because music does not exist: " + updatedMusic);
        }
        Music existingMusic = getById(updatedMusic.getId(),username);
        if (verifyIfMusicUpdated(existingMusic, updatedMusic)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingMusic+ "Updated Book: " + updatedMusic);
        }
        updatedMusic = cloneMusic(updatedMusic);
        updatedMusic.setId(existingMusic.getId());
        updatedMusic.setVersion(existingMusic.getVersion() + 1);
        return dao.createOrUpdate(updatedMusic);
    }

    public Music deleteById(Long id, String username){
        Music music = getById(id, username);
        if (music == null) {
            throw new ResourceNotFoundException("Cannot delete music because music does not exist.");
        }
        dao.deleteById(id, username);
        return music;
    }

    private boolean musicAlreadyExists(Music music, String username) {
        return getAllMusicByArtist(music.getArtists(), username)
                .stream()
                .anyMatch(m -> music.getTitle().equals(m.getTitle()));
    }

    private Music cloneMusic(Music music) {
        Music clonedMusic = new Music();
        BeanUtils.copyProperties(music, clonedMusic);
        return clonedMusic;
    }

    private boolean verifyIfMusicUpdated(Music existingMusic, Music updatedMusic) {
        return existingMusic.equals(updatedMusic);
    }
}
