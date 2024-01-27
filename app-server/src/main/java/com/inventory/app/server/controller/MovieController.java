package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.MediaId;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.MovieMapper;
import com.inventory.app.server.service.media.MovieService;
import com.inventory.app.server.utility.RestPreConditions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/movies")
@Log4j2
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    ResponseEntity<List<MediaResponse>> findAllMovies() {
        try{
            log.info("Recieved a request to get all movies");
            List<MediaResponse> responseList = movieService.getAllMovies().stream()
                    .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error " + e);
        }
    }

    @GetMapping(value = "/{directors}")
    ResponseEntity<List<MediaResponse>> findByDirector(@PathVariable("directors") final List<String> directors) {
        try {
            if (directors.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Directors cannot be empty.");
            }
            List<Movie> moviesByDirectors = movieService.getAllMoviesByDirectors(directors);
            if (moviesByDirectors.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + directors);
            }
            List<MediaResponse> responseList = moviesByDirectors.stream()
                    .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createMovie(@RequestBody final MediaRequest movieRequest) {
        try {
            // Validate mediaId input
            RestPreConditions.validateCreateMediaId(movieRequest.getMediaId());
            // Validate additional attributes
            validatedAdditionalAttributes(movieRequest);
            log.info("Received a request to create resource: " + movieRequest);
            Movie movie = MovieMapper.INSTANCE.mapMediaRequestToMovie(movieRequest);
            MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.create(movie));
            log.info("Created new movie: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e);
        }
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateMovie(@RequestBody final MediaRequest movieRequet) {
        try {
            // validated MediaId
            RestPreConditions.validateUpdateMediaId(movieRequet.getMediaId());
            // validate artists, song list, and release date
            validatedAdditionalAttributes(movieRequet);
            log.info("received request to update resource: " + movieRequet);
            Movie updatedMovie = MovieMapper.INSTANCE.mapMediaRequestToMovie(movieRequet);
            MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.update(updatedMovie));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteMovie(@PathVariable("id") final Long id){
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
            }
            movieService.deleteById(id);
            MediaResponse response = MediaResponse.builder().mediaId(MediaId.builder().id(id).build()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    private void validatedAdditionalAttributes(MediaRequest musicRequest) {
        @SuppressWarnings("unchecked")
        List<String> artists = (List<String>) musicRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey());
        List<String> songList = (List<String>) musicRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey());
        LocalDate releaseDate = (LocalDate) musicRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey());

        if (artists == null || artists.isEmpty() || songList == null || songList.isEmpty() || releaseDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Artists, songlist, and release date must not be null or empty.");
        }
    }
}
