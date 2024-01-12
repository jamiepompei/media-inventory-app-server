package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.repository.media.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicService {
    private MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Music> findAllByArtist(String artist){
       return musicRepository.findByArtist(artist);
    }

    public List<Music> findAllByGenre(String genre){
        return musicRepository.findByGenre(genre);
    }

    public List<Music> finalAllBySong(String song){
        return musicRepository.findSong(song);
    }
}
