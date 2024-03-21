package com.inventory.app.server.controller.media;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findAllMovies() {
        List<MediaResponse> responseList = movieService.getAll().stream()
                .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }


    @GetMapping(value = "/{directors}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByDirector(@PathVariable("directors") final List<String> directors) {
        if (directors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Directors cannot be empty.");
        }
        List<Movie> moviesByDirectors = movieService.getAllMoviesByDirectors(directors);
        List<MediaResponse> responseList = moviesByDirectors.stream()
                .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{title}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByTitle(@PathVariable("title") final String title) {
        if (title.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Title cannot be empty.");
        }
        List<Movie> moviesByTitle = movieService.getAllMoviesByTitle(title);
        List<MediaResponse> responseList = moviesByTitle.stream()
                .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{genre}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByGenre(@PathVariable("genre") final String genre) {
        if (genre.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Genre cannot be empty.");
        }
        List<Movie> moviesByGenre = movieService.getAllMoviesByGenre(genre);
        List<MediaResponse> responseList = moviesByGenre.stream()
                .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{collectionTitle}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByCollectionTitle(@PathVariable("collectionTitle") final String collectionTitle) {
        if (collectionTitle.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Collection Title cannot be empty.");
        }
        List<Movie> moviesByGenre = movieService.getAllMoviesByCollectionTitle(collectionTitle);
        List<MediaResponse> responseList = moviesByGenre.stream()
                .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> createMovie(@Valid @RequestBody final MediaRequest movieRequest) {
        log.info("Received a request to create resource: " + movieRequest);
        Movie movie = MovieMapper.INSTANCE.mapMediaRequestToMovie(movieRequest);
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.create(movie));
        log.info("Created new movie: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> updateMovie(@Valid @RequestBody final MediaRequest mediaRequest) {
        log.info("received request to update resource: " + mediaRequest);
        Movie updatedMovie = MovieMapper.INSTANCE.mapMediaRequestToMovie(mediaRequest);
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.update(updatedMovie));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> deleteMovie(@PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.deleteById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
