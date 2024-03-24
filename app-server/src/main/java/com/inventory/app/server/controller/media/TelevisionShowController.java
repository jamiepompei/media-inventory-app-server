package com.inventory.app.server.controller.media;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findAllTelevisionShows(@AuthenticationPrincipal UserDetails userDetails) {
        List<MediaResponse> responseList = televisionService.getAll(userDetails.getUsername()).stream()
                .map(t -> TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(t))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{episodes}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByEpisode(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("episodes") final List<String> episodes) {
        if (episodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Writers cannot be empty.");
        }
        List<TelevisionShow> televisionShowsByEpisodes = televisionService.getAllTelevisionShowsByEpisode(episodes, userDetails.getUsername());
        List<MediaResponse> responseList = televisionShowsByEpisodes.stream()
                .map(t -> TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(t))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> createTelevisionShow(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final MediaRequest televisionShowRequest) {
        log.info("Received request to create resource: " + televisionShowRequest);
        TelevisionShow televisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(televisionShowRequest);
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.create(televisionShow, userDetails.getUsername()));
        log.info("Created new television show: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> updateTelevisionShow(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final MediaRequest televisionShowRequest) {
        log.info("Received request to update resource: " + televisionShowRequest);
        TelevisionShow updatedTelevisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(televisionShowRequest);
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.update(updatedTelevisionShow, userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<MediaResponse> deleteTelevisionShow(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponseWithAdditionalAttributes(televisionService.deleteById(id, userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
