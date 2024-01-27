package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.utility.RestPreConditions;
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

    public List<Music> getAllMusic() {
        return dao.findAll();
    }

    public Music getMusicById(Long id){
        return dao.findOne(id);
    }

    public Music create(Music music) {
        // validations before performing create
        RestPreConditions.checkAlreadyExists(musicAlreadyExists(music), music);

        Music musicToSave = cloneMusic(music);
        musicToSave.setId(null);
        musicToSave.setVersion(1);

        return dao.createOrUpdate(musicToSave);
    }

    public Music update(Music updatedMusic) {
        //validations before performing the update
        Music existingMusic = RestPreConditions.checkFound(getMusicById(updatedMusic.getId()));
        RestPreConditions.checkEquals(existingMusic, updatedMusic);

        updatedMusic = cloneMusic(existingMusic, updatedMusic);
        updatedMusic.setVersion(existingMusic.getVersion() + 1);

        return dao.createOrUpdate(updatedMusic);
    }

    public void deleteById(Long id){
        RestPreConditions.checkFound(getMusicById(id));
        dao.deleteById(id);
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
}
