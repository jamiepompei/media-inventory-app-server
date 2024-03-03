package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MovieService {
    private IBaseDao<Movie> dao;

    @Autowired
    public void setDao(IBaseDao<Movie> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Movie.class);
    }

    public List<Movie> getAllMoviesByGenre(String genre){
        List<Movie> movieList = dao.findByField("genre", genre);
        if (movieList.isEmpty()) {
            throw new ResourceNotFoundException("No movie results found for genre " + genre);
        }
        return movieList;
    }

    public List<Movie> getAllMoviesByTitle(String title){
        List<Movie> movieList = dao.findByField("title", title);
        if (movieList.isEmpty()) {
            throw new ResourceNotFoundException("No movie results found for title " + title);
        }
        return movieList;
    }

    public List<Movie> getAllMoviesByDirectors(List<String> directors){
        List<Movie> movieList = dao.findByField("directors", directors);
        if (movieList.isEmpty()) {
            throw new ResourceNotFoundException("No movie results found by directors " + directors);
        }
        return movieList;
    }

    public List<Movie> getAllMoviesByCollectionTitle(String collectionTitle){
        List<Movie> movieList = dao.findByField("title", collectionTitle);
        if (movieList.isEmpty()) {
            throw new ResourceNotFoundException("No movie results found for collection title " + collectionTitle);
        }
        return movieList;
    }

    public List<Movie> getAll() {
        List<Movie> movieList = dao.findAll();
        if (movieList.isEmpty()) {
            throw new ResourceNotFoundException("No movie data exists.");
        }
        return movieList;
    }

    public Movie getById(Long id) {
        try {
            return dao.findOne(id);
        } catch (Exception e) {
            if (e.getClass().isInstance(EntityNotFoundException.class)){
                throw new ResourceNotFoundException("No movie exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public Movie create(Movie movie) {
       if (movieAlreadyExists(movie)) {
           throw new ResourceAlreadyExistsException("Cannot create movie because movie already exists: " + movie);
       }
        Movie movieToSave = cloneMovie(movie);
        movieToSave.setId(null);
        movieToSave.setVersion(1);
        return dao.createOrUpdate(movieToSave);
    }

    public Movie update(Movie updatedMovie) {
        if (!movieAlreadyExists(updatedMovie)) {
            throw new ResourceNotFoundException("Cannot update movie because movie does not exist: " + updatedMovie);
        }
        Movie existingMovie = getById(updatedMovie.getId());
        if (verifyIfMovieUpdated(existingMovie, updatedMovie)) {
            throw new NoChangesToUpdateException("No updates in movie to save. Will not proceed with update. Existing Movie: " + existingMovie + "Updated Movie: " + updatedMovie);
        }
        updatedMovie = cloneMovie(updatedMovie);
        updatedMovie.setId(existingMovie.getId());
        updatedMovie.setVersion(existingMovie.getVersion() + 1);
        return dao.createOrUpdate(updatedMovie);
    }

    public Movie deleteById(Long id){
        Movie movie = getById(id);
        if (movie == null ) {
            throw new ResourceNotFoundException("Cannot delete movie because movie does not exist.");
        }
        dao.deleteById(id);
        return movie;
    }

    private boolean movieAlreadyExists(Movie movie) {
        return getAllMoviesByDirectors(movie.getDirectors())
                .stream()
                .anyMatch(m -> movie.getTitle().equals(m.getTitle()));
    }

    private Movie cloneMovie(Movie movie) {
        Movie clonedMovie = new Movie();
        BeanUtils.copyProperties(movie, clonedMovie);
        return clonedMovie;
    }

    private boolean verifyIfMovieUpdated(Movie existingMovie, Movie updatedMovie) {
        return existingMovie.equals(updatedMovie);
    }
}



