
package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.GameMapper;
import com.inventory.app.server.service.media.GameService;
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
public class GameController {
    //TODO figure out the logs for exceptions

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    @GetMapping
    ResponseEntity<List<MediaResponse>> searchGames(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        List<MediaResponse> responseList = gameService.searchGames(searchMediaRequest).stream()
                .map(b -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createGame(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest gameRequest) {
        log.info("Received request to create resource: " + gameRequest);
        Game game = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.create(game));
        log.info("Created new game: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateGame(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest gameRequest) {
        log.info("received request to update resource: " + gameRequest);
        Game updatedGame = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.update(updatedGame));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteGame(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.deleteById(id, userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

