package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.utility.RestPreConditions;
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

    public List<Movie> getAllMovies() { return dao.findAll();}

    public Movie getMovieById(Long id) { return dao.findOne(id);}

    public Movie create(Movie movie) {
        // validations before performing create
        RestPreConditions.checkAlreadyExists(movieAlreadyExists(movie), movie);

        Movie movieToSave = cloneMovie(movie);
        movieToSave.setId(null);
        movieToSave.setVersion(1);

        return dao.createOrUpdate(movieToSave);
    }

    public Movie update(Movie updatedMovie) {
        //validations before performing the update
        Movie existingMovie = RestPreConditions.checkFound(getMovieById(updatedMovie.getId()));
        RestPreConditions.checkEquals(existingMovie, updatedMovie);

        updatedMovie = cloneMovie(existingMovie, updatedMovie);
        updatedMovie.setVersion(existingMovie.getVersion() + 1);

        return dao.createOrUpdate(updatedMovie);
    }

    public Movie deleteById(Long id){
        Movie movie = RestPreConditions.checkFound(getMovieById(id));
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
}



