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
public class MovieService implements BaseService<Movie> {
    private IBaseDao<Movie> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Movie> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Movie.class);
    }

    public List<MediaResponse> search(SearchMediaRequest searchMediaRequest){
        Optional<Predicate<Movie>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        return searchPredicate.map(moviePredicate -> dao.findAll().stream()
                .filter(moviePredicate)
                .map(MovieMapper.INSTANCE::mapMovieToMediaResponse)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
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
        return Optional.of(predicate);
    }

    public Optional<Movie> getById(Long id, String username) {
       Movie movie = dao.findOne(id, username);
       return movie == null ? Optional.empty() : Optional.of(movie);
    }

    public MediaResponse create(UpdateCreateMediaRequest updateCreateMediaRequest) {
        Movie movie = MovieMapper.INSTANCE.mapMediaRequestToMovie(updateCreateMediaRequest);
       Optional<Movie> existingMovie = getById(movie.getId(), movie.getCreatedBy());
       if (existingMovie.isPresent()) {
           throw new ResourceAlreadyExistsException("Cannot create movie because movie already exists: " + movie);
       }
       return MovieMapper.INSTANCE.mapMovieToMediaResponse(dao.createOrUpdate(movie));
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
        return MovieMapper.INSTANCE.mapMovieToMediaResponse(dao.createOrUpdate(updatedMovie));
    }

    public Long deleteById(Long id, String username){
        Optional<Movie> movie = getById(id, username);
        if (movie.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete movie because movie does not exist.");
        }
        dao.deleteById(id, username);
        return movie.get().getId();
    }

    private boolean verifyIfMovieUpdated(Movie existingMovie, Movie updatedMovie) {
        return existingMovie.equals(updatedMovie);
    }
}



