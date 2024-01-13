package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.repository.media.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    List<Movie> findAllByGenre(String genre){
        return movieRepository.findByGenre(genre);
    }

    List<Movie> findAllByTitle(String title){
        return movieRepository.findByTitle(title);
    }
}
