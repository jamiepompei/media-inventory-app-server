package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.payload.request.MediaId;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.TelevisionShowMapper;
import com.inventory.app.server.service.media.TelevisionService;
import com.inventory.app.server.utility.RestPreConditions;
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
        try {
            log.info("Received a request to get all books");
            List<MediaResponse> responseList = televisionService.getAllTelevisionShows().stream()
                    .map(t -> TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(t))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error " + e);
        }
    }

    @GetMapping(value = "/{writers}")
    ResponseEntity<List<MediaResponse>> findByWriter(@PathVariable("writers") final List<String> writers) {
        try {
            if (writers.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Writers cannot be empty.");
            }
            List<TelevisionShow> televisionShowsByWriter = televisionService.getAllTelevisionShowsByWriter(writers);
            if (televisionShowsByWriter.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + writers);
            }
            List<MediaResponse> responseList = televisionShowsByWriter.stream()
                    .map(t -> TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(t))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> creatTelevisionShow(@RequestBody final MediaRequest televisionShowRequest) {
        try {
            // Validate mediaId input
            RestPreConditions.validateCreateMediaId(televisionShowRequest.getMediaId());
            // Validate additional attributes
            validatedAdditionalAttributes(televisionShowRequest);
            log.info("Received request to create resource: " + televisionShowRequest);
            TelevisionShow televisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(televisionShowRequest);
            MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.create(televisionShow));
            log.info("Created new television show: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e);
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateTelevisionShow(@RequestBody final MediaRequest televisionShowRequest) {
        try {
            // Validate MediaId
            RestPreConditions.validateUpdateMediaId(televisionShowRequest.getMediaId());
            // Validate authors, copyright year, and edition
            validatedAdditionalAttributes(televisionShowRequest);
            log.info("received request to update resource: " + televisionShowRequest);
            TelevisionShow updatedTelevisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(televisionShowRequest);
            MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.update(updatedTelevisionShow));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteTelevisionShow(@PathVariable("id") final Long id){
        try{
            if (id == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
            }
            televisionService.deleteById(id);
            MediaResponse response = MediaResponse.builder().mediaId(MediaId.builder().id(id).build()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    private void validatedAdditionalAttributes(MediaRequest televisionShowRequest) {
        @SuppressWarnings("unchecked")
        List<String> writers = (List<String>) televisionShowRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey());
        Integer season = (Integer) televisionShowRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.SEASON.getJsonKey());
        Integer releaseDate = (Integer) televisionShowRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey());

        if (writers == null || writers.isEmpty() || season == null || releaseDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Writers, season, and release date must not be null or empty.");
        }
    }
}
