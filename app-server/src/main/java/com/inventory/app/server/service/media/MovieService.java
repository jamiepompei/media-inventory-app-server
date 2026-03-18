package com.inventory.app.server.service.media;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.mapper.MovieMapper;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MovieService implements BaseService<Movie> {
    private IBaseDao<Movie> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Movie> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Movie.class);
    }

    public List<MediaResponse> search(SearchMediaRequest searchMediaRequest){
        log.info("Initiating search for movies with search criteria: " + searchMediaRequest);
        Optional<Predicate<Movie>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        List<MediaResponse> mediaResponses = searchPredicate.map(moviePredicate -> dao.findAll().stream()
                .filter(moviePredicate)
                .map(MovieMapper.INSTANCE::mapMovieToMediaResponse)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        if (mediaResponses.isEmpty()){
            log.info("No movies found matching search criteria: " + searchMediaRequest);
        } else {
            log.info("Search completed successfully. Number of movies found: " + mediaResponses.size());
        }
        return mediaResponses;
    }

    private Optional<Predicate<Movie>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Movie> predicate = movie -> true; // Default Predicate

        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(movie -> movie.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(movie -> movie.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(movie -> movie.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(movie -> movie.getCreatedBy().equals(searchMediaRequest.getUsername()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(movie -> movie.getDirectors().equals(searchMediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey()) != null) {
            predicate = predicate.and(movie -> movie.getReleaseYear().equals(searchMediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey())));
        }
        log.info("Search Predicate: {} built successfully for search criteria: {}", predicate, searchMediaRequest);
        return Optional.of(predicate);
    }

    public Optional<Movie> getById(Long id, String username) {
        log.info("Initiating retrieval of movie with id: {} for user: {}", id, username);
       Movie movie = dao.findOne(id, username);
       if (movie == null) {
           log.warn("Movie with id: {} not found for user: {}", id, username);
           return Optional.empty();
       } else {
           log.info("Movie with id: {} retrieved successfully for user: {}", id, username);
           return Optional.of(movie);
       }
    }

    public MediaResponse create(UpdateCreateMediaRequest updateCreateMediaRequest) {
        Movie movie = MovieMapper.INSTANCE.mapMediaRequestToMovie(updateCreateMediaRequest);
       Optional<Movie> existingMovie = getById(movie.getId(), movie.getCreatedBy());
       if (existingMovie.isPresent()) {
           log.warn("Attempting to create a movie with an id that already exists. Movie: {}", movie);
           throw new ResourceAlreadyExistsException("Cannot create movie because movie already exists: " + movie);
       }
       log.info("Initiating movie POST request. Movie to be created: {}", movie);
       MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponse(dao.createOrUpdate(movie));
       log.info("Movie created successfully with ID: {}", movie.getId());
       return response;
    }

    public MediaResponse update(UpdateCreateMediaRequest updateCreateMediaRequest) {
        Movie updatedMovie = MovieMapper.INSTANCE.mapMediaRequestToMovie(updateCreateMediaRequest);
        Optional<Movie> existingMovie = getById(updatedMovie.getId(), updatedMovie.getCreatedBy());
        if (existingMovie.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update movie because no movie exists " + updatedMovie);
        }
        if (verifyIfMovieUpdated(existingMovie.get(), updatedMovie)) {
            throw new NoChangesToUpdateException("No updates in movie to save. Will not proceed with update. Existing movie: " + existingMovie + " Update Movie: " + updatedMovie);
        }
        log.info("Initiating movie PUT request. Movie to be updated: {}", updatedMovie);
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponse(dao.createOrUpdate(updatedMovie));
        log.info("Movie with ID: {} updated successfully", updatedMovie.getId());
        return response;
    }

    public Long deleteById(Long id, String username){
        log.info("Initiating movie DELETE request for movie with ID: {} by user: {}", id, username);
        Optional<Movie> movie = getById(id, username);
        if (movie.isEmpty()) {
            log.warn("Attempting to delete a movie that does not exist. Movie ID: {}, User: {}", id, username);
            throw new ResourceNotFoundException("Cannot delete movie because movie does not exist.");
        }
        dao.deleteById(id, username);
        log.info("Movie with ID: {} deleted successfully by user: {}", id, username);
        return movie.get().getId();
    }

    private boolean verifyIfMovieUpdated(Movie existingMovie, Movie updatedMovie) {
        return existingMovie.equals(updatedMovie);
    }
}



