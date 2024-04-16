package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.DIRECTORS;
import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.RELEASE_YEAR;

@Service
@Transactional
public class MovieService {
    private IBaseDao<Movie> dao;

    @Autowired
    public void setDao(IBaseDao<Movie> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Movie.class);
    }

    public List<Movie> searchMovies(SearchMediaRequest searchMediaRequest){
        Optional<Predicate<Movie>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        return searchPredicate.map(moviePredicate -> dao.findAll().stream()
                .filter(moviePredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Predicate<Movie>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Movie> predicate = movie -> true; // Default Predicate
        if (searchMediaRequest.getCollectionTitle() != null && !searchMediaRequest.getCollectionTitle().isEmpty()) {
            predicate = predicate.and(movie -> movie.getCollectionTitle().equals(searchMediaRequest.getCollectionTitle()));
        }
        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(movie -> movie.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(movie -> movie.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(movie -> movie.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(DIRECTORS.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(DIRECTORS.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(movie -> movie.getDirectors().equals(searchMediaRequest.getAdditionalAttributes().get(DIRECTORS.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey()) != null) {
            predicate = predicate.and(movie -> movie.getReleaseYear().equals(searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey())));
        }
        return Optional.of(predicate);
    }

    public Optional<Movie> getById(Long id, String username) {
       return Optional.of(dao.findOne(id, username));
    }

    public Movie create(Movie movie) {
       Optional<Movie> existingMovie = getById(movie.getId(), movie.getCreatedBy());
       if (existingMovie.isPresent()) {
           throw new ResourceAlreadyExistsException("Cannot create movie because movie already exists: " + movie);
       }
       return dao.createOrUpdate(movie);
    }

    public Movie update(Movie updatedMovie) {
        Optional<Movie> existingMovie = getById(updatedMovie.getId(), updatedMovie.getCreatedBy());
        if (existingMovie.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update movie because no movie exists " + updatedMovie);
        }
        if (verifyIfMovieUpdated(existingMovie.get(), updatedMovie)) {
            throw new NoChangesToUpdateException("No updates in movie to save. Will not proceed with update. Existing movie: " + existingMovie + " Update Movie: " + updatedMovie);
        }
        return dao.createOrUpdate(updatedMovie);
    }

    public Movie deleteById(Long id, String username){
        Optional<Movie> movie = getById(id, username);
        if (movie.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete movie because movie does not exist.");
        }
        dao.deleteById(id, username);
        return movie.get();
    }

    private boolean verifyIfMovieUpdated(Movie existingMovie, Movie updatedMovie) {
        return existingMovie.equals(updatedMovie);
    }
}



