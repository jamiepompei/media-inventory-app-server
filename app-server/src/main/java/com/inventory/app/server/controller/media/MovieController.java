
package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.MovieMapper;
import com.inventory.app.server.service.media.MovieService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    ResponseEntity<List<MediaResponse>> searchMovies(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        List<MediaResponse> responseList = movieService.searchMovies(searchMediaRequest).stream()
                .map(m -> MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> createMovie(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest movieRequest) {
        log.info("Received a request to create resource: " + movieRequest);
        Movie movie = MovieMapper.INSTANCE.mapMediaRequestToMovie(movieRequest);
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.create(movie));
        log.info("Created new movie: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> updateMovie(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest mediaRequest) {
        log.info("received request to update resource: " + mediaRequest);
        Movie updatedMovie = MovieMapper.INSTANCE.mapMediaRequestToMovie(mediaRequest);
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.update(updatedMovie));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> deleteMovie(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = MovieMapper.INSTANCE.mapMovieToMediaResponseWithAdditionalAttributes(movieService.deleteById(id, userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}