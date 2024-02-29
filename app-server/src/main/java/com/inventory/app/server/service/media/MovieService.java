package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
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
        return dao.findByField("genre", genre);
    }

    public List<Movie> getAllMoviesByTitle(String title){
        return dao.findByField("title", title);
    }

    public List<Movie> getAllMoviesByDirectors(List<String> directors){
        return dao.findByField("directors", directors);
    }

    public List<Movie> getAllMoviesByCollectionTitle(String collectionTitle){
        return dao.findByField("title", collectionTitle);
    }

    public List<Movie> getAll() { return dao.findAll();}

    public Movie getById(Long id) { return dao.findOne(id);}

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
        updatedMovie = cloneMovie(existingMovie, updatedMovie);
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

    private Movie cloneMovie(Movie existingMovie, Movie updatedMovie) {
        BeanUtils.copyProperties(updatedMovie, existingMovie);
        return existingMovie;
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



