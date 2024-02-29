package com.inventory.app.server.controller;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.TelevisionShowMapper;
import com.inventory.app.server.service.media.TelevisionService;
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
@RequestMapping(value = "/televisionShows")
@Log4j2
public class TelevisionShowController {

    private TelevisionService televisionService;

    @Autowired
    public TelevisionShowController(TelevisionService televisionService) {
        this.televisionService = televisionService;
    }

    @GetMapping
    ResponseEntity<List<MediaResponse>> findAllTelevisionShows() {
        List<MediaResponse> responseList = televisionService.getAll().stream()
                .map(t -> TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(t))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{episodes}")
    ResponseEntity<List<MediaResponse>> findByEpisode(@PathVariable("episodes") final List<String> episodes) {
        if (episodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Writers cannot be empty.");
        }
        List<TelevisionShow> televisionShowsByEpisodes = televisionService.getAllTelevisionShowsByEpisode(episodes);
        List<MediaResponse> responseList = televisionShowsByEpisodes.stream()
                .map(t -> TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(t))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createTelevisionShow(@Valid @RequestBody final MediaRequest televisionShowRequest) {
        log.info("Received request to create resource: " + televisionShowRequest);
        TelevisionShow televisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(televisionShowRequest);
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.create(televisionShow));
        log.info("Created new television show: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateTelevisionShow(@Valid @RequestBody final MediaRequest televisionShowRequest) {
        log.info("Received request to update resource: " + televisionShowRequest);
        TelevisionShow updatedTelevisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(televisionShowRequest);
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.update(updatedTelevisionShow));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteTelevisionShow(@PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.deleteById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
