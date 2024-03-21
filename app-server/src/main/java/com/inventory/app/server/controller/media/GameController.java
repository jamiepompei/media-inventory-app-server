package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.MediaRequest;
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

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findAllGames(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received a request to get all games");
        List<MediaResponse> responseList = gameService.getAllByUsername(userDetails.getUsername()).stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{consoles}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByConsole(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("consoles") final List<String> consoles) {
        if (consoles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Consoles cannot be empty.");
        }
        List<Game> gamesByConsole = gameService.getAllGamesByConsole(consoles, userDetails.getUsername());
        List<MediaResponse> responseList = gamesByConsole.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{title}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByTitle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("title") final String title) {
        if (title.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Title cannot be empty.");
        }
        List<Game> gamesByTitle = gameService.getAllGamesByTitle(title, userDetails.getUsername());
        List<MediaResponse> responseList = gamesByTitle.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{collectionTitle}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByCollectionTitle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("collectionTitle") final String collectionTitle) {
        if (collectionTitle.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Collection title cannot be empty.");
        }
        List<Game> gamesByCollectionTitle = gameService.getAllGamesByCollectionTitle(collectionTitle, userDetails.getUsername());
        List<MediaResponse> responseList = gamesByCollectionTitle.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{genre}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByGenre(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("genre") final String genre) {
        if (genre.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Genre cannot be empty.");
        }
        List<Game> gamesByGenre = gameService.getAllGamesByGenre(genre, userDetails.getUsername());
        List<MediaResponse> responseList = gamesByGenre.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{numberOfPlayers}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    ResponseEntity<List<MediaResponse>> findByNumberOfPlayers(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("numberOfPlayers") final Integer numberOfPlayers) {
        if (numberOfPlayers == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors cannot be empty.");
        }
        List<Game> gamesByNumberOfPlayers = gameService.getAllGamesByNumberOfPlayers(numberOfPlayers, userDetails.getUsername());
        List<MediaResponse> responseList = gamesByNumberOfPlayers.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createGame(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final MediaRequest gameRequest) {
        log.info("Received request to create resource: " + gameRequest);
        Game game = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.create(game, userDetails.getUsername()));
        log.info("Created new game: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateGame(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final MediaRequest gameRequest) {
        log.info("received request to update resource: " + gameRequest);
        Game updatedGame = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.update(updatedGame, userDetails.getUsername()));
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
