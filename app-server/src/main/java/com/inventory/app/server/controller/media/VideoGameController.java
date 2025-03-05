
package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.media.VideoGame;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.VideoGameMapper;
import com.inventory.app.server.service.media.VideoGameService;
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
@RequestMapping(value = "/games")
@Log4j2
public class VideoGameController {
    //TODO figure out the logs for exceptions

    private VideoGameService videoGameService;

    @Autowired
    public VideoGameController(VideoGameService videoGameService) {
        this.videoGameService = videoGameService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    @GetMapping
    ResponseEntity<List<MediaResponse>> searchGames(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        List<MediaResponse> responseList = videoGameService.searchGames(searchMediaRequest).stream()
                .map(VideoGameMapper.INSTANCE::mapGameToMediaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createGame(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest gameRequest) {
        log.info("Received request to create resource: " + gameRequest);
        VideoGame game = VideoGameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = VideoGameMapper.INSTANCE.mapGameToMediaResponse(videoGameService.create(game));
        log.info("Created new game: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateGame(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest gameRequest) {
        log.info("received request to update resource: " + gameRequest);
        VideoGame updatedGame = VideoGameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = VideoGameMapper.INSTANCE.mapGameToMediaResponse(videoGameService.update(updatedGame));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteGame(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = VideoGameMapper.INSTANCE.mapGameToMediaResponse(videoGameService.deleteById(id, userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

