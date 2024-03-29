package com.inventory.app.server.controller;

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
    ResponseEntity<List<MediaResponse>> findAllGames() {
        log.info("Received a request to get all games");
        List<MediaResponse> responseList = gameService.getAll().stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{consoles}")
    ResponseEntity<List<MediaResponse>> findByConsole(@PathVariable("consoles") final List<String> consoles) {
        if (consoles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Consoles cannot be empty.");
        }
        List<Game> gamesByConsole = gameService.getAllGamesByConsole(consoles);
        List<MediaResponse> responseList = gamesByConsole.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{title}")
    ResponseEntity<List<MediaResponse>> findByTitle(@PathVariable("title") final String title) {
        if (title.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Title cannot be empty.");
        }
        List<Game> gamesByTitle = gameService.getAllGamesByTitle(title);
        List<MediaResponse> responseList = gamesByTitle.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{collectionTitle}")
    ResponseEntity<List<MediaResponse>> findByCollectionTitle(@PathVariable("collectionTitle") final String collectionTitle) {
        if (collectionTitle.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Collection title cannot be empty.");
        }
        List<Game> gamesByCollectionTitle = gameService.getAllGamesByCollectionTitle(collectionTitle);
        List<MediaResponse> responseList = gamesByCollectionTitle.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{genre}")
    ResponseEntity<List<MediaResponse>> findByGenre(@PathVariable("genre") final String genre) {
        if (genre.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Genre cannot be empty.");
        }
        List<Game> gamesByGenre = gameService.getAllGamesByGenre(genre);
        List<MediaResponse> responseList = gamesByGenre.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{numberOfPlayers}")
    ResponseEntity<List<MediaResponse>> findByNumberOfPlayers(@PathVariable("numberOfPlayers") final Integer numberOfPlayers) {
        if (numberOfPlayers == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors cannot be empty.");
        }
        List<Game> gamesByNumberOfPlayers = gameService.getAllGamesByNumberOfPlayers(numberOfPlayers);
        List<MediaResponse> responseList = gamesByNumberOfPlayers.stream()
                .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createGame(@Valid @RequestBody final MediaRequest gameRequest) {
        log.info("Received request to create resource: " + gameRequest);
        Game game = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.create(game));
        log.info("Created new game: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateGame(@Valid @RequestBody final MediaRequest gameRequest) {
        log.info("received request to update resource: " + gameRequest);
        Game updatedGame = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.update(updatedGame));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteGame(@PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.deleteById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
