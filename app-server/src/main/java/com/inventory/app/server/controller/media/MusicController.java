
package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.MusicMapper;
import com.inventory.app.server.service.media.MusicService;
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
@RequestMapping(value = "/music")
@Log4j2
public class MusicController {

    private MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> searchMusic(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        List<MediaResponse> responseList = musicService.searchMusic(searchMediaRequest).stream()
                .map(m -> MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(m))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> createMusic(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest musicRequest) {
        log.info("Received a request to create resource: " + musicRequest);
        Music music = MusicMapper.INSTANCE.mapMediaRequestToMusic(musicRequest);
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.create(music));
        log.info("Created new music: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> updateMusic(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest musicRequest) {
        log.info("received request to update resource: " + musicRequest);
        Music updatedMusic = MusicMapper.INSTANCE.mapMediaRequestToMusic(musicRequest);
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.update(updatedMusic));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    public ResponseEntity<MediaResponse> deleteMusic(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = MusicMapper.INSTANCE.mapMusicToMediaResponseWithAdditionalAttributes(musicService.deleteById(id, userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

