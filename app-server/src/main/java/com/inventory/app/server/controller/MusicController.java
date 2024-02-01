package com.inventory.app.server.controller;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.MusicMapper;
import com.inventory.app.server.service.media.MusicService;
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
@RequestMapping(value = "/music")
@Log4j2
public class MusicController {

    private MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping
    ResponseEntity<List<MediaResponse>> findAllMusic() {
        try{
            log.info("Received a request to get all music");
            List<MediaResponse> responseList = musicService.getAllMusic().stream()
                    .map(m -> MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(m))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error " + e);
        }
    }

    @GetMapping(value = "/{artists}")
    ResponseEntity<List<MediaResponse>> findByArtist(@PathVariable("artists") final List<String> artists) {
        try {
            if (artists.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Artists cannot be empty.");
            }
            List<Music> musicByArtists = musicService.getAllMusicByArtist(artists);
            if (musicByArtists.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + artists);
            }
            List<MediaResponse> responseList = musicByArtists.stream()
                    .map(m -> MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(m))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createMusic(@Valid @RequestBody final MediaRequest musicRequest) {
        try {
            log.info("Received a request to create resource: " + musicRequest);
            Music music = MusicMapper.INSTANCE.mapMediaRequestToMusic(musicRequest);
            MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.create(music));
            log.info("Created new music: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e);
        }
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateMusic(@Valid @RequestBody final MediaRequest musicRequest) {
        try {
            log.info("received request to update resource: " + musicRequest);
            Music updatedMusic = MusicMapper.INSTANCE.mapMediaRequestToMusic(musicRequest);
            MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.update(updatedMusic));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteMusic(@PathVariable("id") final Long id){
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
            }
            MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.deleteById(id));
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
