package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.MediaId;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.GameMapper;
import com.inventory.app.server.service.media.GameService;
import com.inventory.app.server.utility.RestPreConditions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/games")
@Log4j2
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    ResponseEntity<List<MediaResponse>> findAllGames() {
        try {
            log.info("Received a request to get all games");
            List<MediaResponse> responseList = gameService.getAllGames().stream()
                    .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error " + e);
        }
    }

    @GetMapping(value = "/{consoles}")
    ResponseEntity<List<MediaResponse>> findByConsole(@PathVariable("consoles") final List<String> consoles) {
        try {
            if (consoles.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors cannot be empty.");
            }
            List<Game> gamesByConsole = gameService.getAllGamesByConsole(consoles);
            if (gamesByConsole.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + consoles);
            }
            List<MediaResponse> responseList = gamesByConsole.stream()
                    .map(g -> GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(g))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createGame(@RequestBody final MediaRequest gameRequest) {
        try {
            // Validate mediaId input
            RestPreConditions.validateCreateMediaId(gameRequest.getMediaId());
            // Validate additional attributes
            validatedAdditionalAttributes(gameRequest);
            log.info("Received request to create resource: " + gameRequest);
            Game game = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
            MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.create(game));
            log.info("Created new game: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e);
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateGame(@RequestBody final MediaRequest gameRequest) {
        try {
            // Validate MediaId
            RestPreConditions.validateUpdateMediaId(gameRequest.getMediaId());
            // Validate authors, copyright year, and edition
            validatedAdditionalAttributes(gameRequest);
            log.info("received request to update resource: " + gameRequest);
            Game updatedGame = GameMapper.INSTANCE.mapMediaRequestToGame(gameRequest);
            MediaResponse response = GameMapper.INSTANCE.mapGameToMediaResponseWithAdditionalAttributes(gameService.update(updatedGame));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteGame(@PathVariable("id") final Long id){
        try{
            if (id == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
            }
            gameService.deleteById(id);
            MediaResponse response = MediaResponse.builder().mediaId(MediaId.builder().id(id).build()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    private void validatedAdditionalAttributes(MediaRequest gameRequest) {
        @SuppressWarnings("unchecked")
        List<String> consoles = (List<String>) gameRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey());
        Integer numberOfPlayers = (Integer) gameRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.NUMBER_OF_PLAYERS.getJsonKey());
        LocalDate releaseDate = (LocalDate) gameRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey());

        if (consoles == null || consoles.isEmpty() || numberOfPlayers == null || releaseDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Consoles, number of players, and release date must not be null or empty.");
        }
    }
}
