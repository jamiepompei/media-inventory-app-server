package com.inventory.app.server.controller;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.MovieMapper;
import com.inventory.app.server.service.media.MovieService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<MediaResponse> createMovie(@Valid @RequestBody final MediaRequest movieRequest) {
        try {
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
    public ResponseEntity<MediaResponse> updateMovie(@Valid @RequestBody final MediaRequest mediaRequest) {
        try {
            log.info("received request to update resource: " + mediaRequest);
            Movie updatedMovie = MovieMapper.INSTANCE.mapMediaRequestToMovie(mediaRequest);
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
            MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.deleteById(id));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
