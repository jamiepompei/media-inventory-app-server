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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
        List<MediaResponse> responseList = musicService.getAll().stream()
                .map(m -> MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{artists}")
    ResponseEntity<List<MediaResponse>> findByArtist(@PathVariable("artists") final List<String> artists) {
        if (artists.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Artists cannot be empty.");
        }
        List<Music> musicByArtists = musicService.getAllMusicByArtist(artists);
        List<MediaResponse> responseList = musicByArtists.stream()
                .map(m -> MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createMusic(@Valid @RequestBody final MediaRequest musicRequest) {
        log.info("Received a request to create resource: " + musicRequest);
        Music music = MusicMapper.INSTANCE.mapMediaRequestToMusic(musicRequest);
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.create(music));
        log.info("Created new music: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateMusic(@Valid @RequestBody final MediaRequest musicRequest) {
        log.info("received request to update resource: " + musicRequest);
        Music updatedMusic = MusicMapper.INSTANCE.mapMediaRequestToMusic(musicRequest);
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.update(updatedMusic));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteMusic(@PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.deleteById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
