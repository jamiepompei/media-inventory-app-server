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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<MediaResponse> createTelevisionShow(@Valid @RequestBody final MediaRequest televisionShowRequest) {
        try {
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
    public ResponseEntity<MediaResponse> updateTelevisionShow(@Valid @RequestBody final MediaRequest televisionShowRequest) {
        try {
            log.info("Received request to update resource: " + televisionShowRequest);
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
            MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.deleteById(id));
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
