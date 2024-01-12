package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.repository.media.TelevisionShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelevisionService {
    private TelevisionShowRepository televisionShowRepository;
    @Autowired
    public TelevisionService(TelevisionShowRepository televisionShowRepository) {
        this.televisionShowRepository = televisionShowRepository;
    }

    List<TelevisionShow> finalAllByGenre(String genre){
        return televisionShowRepository.findByGenre(genre);
    }

    List<TelevisionShow> findAllByTitle(String title) {
        return televisionShowRepository.findByTitle(title);
    }
}
