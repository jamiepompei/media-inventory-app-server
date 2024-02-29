package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
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

    public List<Music> getAllMusicByArtist(List<String> artist){
       return dao.findByField("artist", artist);
    }

    public List<Music> getAllMusicByGenre(String genre){
        return dao.findByField("genre", genre);
    }

    public List<Music> getAllMusicByCollectionTitle(String collectionTitle){
        return dao.findByField("collection_name", collectionTitle);
    }

    public List<Music> getAll() {
        return dao.findAll();
    }

    public Music getById(Long id){
        return dao.findOne(id);
    }

    public Music create(Music music) {
       if (musicAlreadyExists(music)) {
           throw new ResourceAlreadyExistsException("Cannot create music because music already exists: " + music);
        }
        Music musicToSave = cloneMusic(music);
        musicToSave.setId(null);
        musicToSave.setVersion(1);
        return dao.createOrUpdate(musicToSave);
    }

    public Music update(Music updatedMusic) {
        if (!musicAlreadyExists(updatedMusic)) {
            throw new ResourceNotFoundException("Cannot update music because music does not exist: " + updatedMusic);
        }
        Music existingMusic = getById(updatedMusic.getId());
        if (verifyIfMusicUpdated(existingMusic, updatedMusic)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingMusic+ "Updated Book: " + updatedMusic);
        }
        updatedMusic = cloneMusic(existingMusic, updatedMusic);
        updatedMusic.setVersion(existingMusic.getVersion() + 1);
        return dao.createOrUpdate(updatedMusic);
    }

    public Music deleteById(Long id){
        Music music = getById(id);
        if (music == null) {
            throw new ResourceNotFoundException("Cannot delete music because music does not exist.");
        }
        dao.deleteById(id);
        return music;
    }

    private boolean musicAlreadyExists(Music music) {
        return getAllMusicByArtist(music.getArtists())
                .stream()
                .anyMatch(m -> music.getTitle().equals(m.getTitle()));
    }

    private Music cloneMusic(Music existingMusic, Music updatedMusic) {
        BeanUtils.copyProperties(updatedMusic, existingMusic);
        return existingMusic;
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
