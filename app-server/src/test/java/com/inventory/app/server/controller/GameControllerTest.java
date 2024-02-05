package com.inventory.app.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.service.media.GameService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc
public class GameControllerTest {

    @MockBean
    private GameService gameService;

    @Autowired
    private GameController underTest;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGameControllerInject_thenNotNull() { assertThat(underTest).isNotNull();}

    @Test
    public void whenPostRequestToGameAndValidGame_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Game mockGame = createGame("Nancy Drew", "Digital Download", "Mystery", Arrays.asList("Computer"),1, "Jamie's Stuff", 2019);

        // Mock the behavior of bookService.create
        when(gameService.create(any())).thenReturn(mockGame);

        ConcurrentHashMap<String, Object> additionalBookAttributes = new ConcurrentHashMap<>();
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey(), mockGame.getConsoles());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockGame.getReleaseYear());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.NUMBER_OF_PLAYERS.getJsonKey(), mockGame.getNumberOfPlayers());
        MediaRequest mediaRequest = MediaRequest.builder()
                .title(mockGame.getTitle())
                .genre(mockGame.getGenre())
                .format(mockGame.getFormat())
                .collectionName(mockGame.getCollectionName())
                .additionalAttributes(additionalBookAttributes)
                .build();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String game = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/games")
                        .content(game)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockGame.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(mockGame.getVersion()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockGame.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockGame.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockGame.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockGame.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.consoles").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.numberOfPlayers").value(mockGame.getNumberOfPlayers()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockGame.getReleaseYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPostRequestGameAndInvalidGame_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();

        // Covert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String game = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/gamess")
                .content(game)
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPuRequestToGameAndValidGame_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Game mockGame = createGame(1L, 1,"Nancy Drew", "Digital Download", "Mystery", Arrays.asList("Computer"),1, "Jamie's Stuff", 2019);

        // Mock the behavior of bookService.create
        when(gameService.update(any())).thenReturn(mockGame);

        ConcurrentHashMap<String, Object> additionalBookAttributes = new ConcurrentHashMap<>();
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey(), mockGame.getConsoles());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockGame.getReleaseYear());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.NUMBER_OF_PLAYERS.getJsonKey(), mockGame.getNumberOfPlayers());
        MediaRequest mediaRequest = MediaRequest.builder()
                .id(mockGame.getId())
                .version(mockGame.getVersion())
                .title(mockGame.getTitle())
                .genre(mockGame.getGenre())
                .format(mockGame.getFormat())
                .collectionName(mockGame.getCollectionName())
                .additionalAttributes(additionalBookAttributes)
                .build();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String game = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/games/{id}", mediaRequest.getId())
                        .content(game)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockGame.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(mockGame.getVersion()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockGame.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockGame.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockGame.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockGame.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.consoles").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.numberOfPlayers").value(mockGame.getNumberOfPlayers()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockGame.getReleaseYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPutRequestGameAndInvalidGame_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();
        mediaRequest.setId(1L);

        // Covert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String game = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/games/{id}", mediaRequest.getId())
                        .content(game)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenGetByConsoleRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        List<String> consoles = Arrays.asList("Switch");
        Game game = new Game();

        when(gameService.getAllGamesByConsole(consoles)).thenReturn(Arrays.asList(game));

        mockMvc.perform(MockMvcRequestBuilders.get("/games/{consoles}",  String.join(",", consoles))
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenGetAllGamesRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Game game = new Game();

        when(gameService.getAllGames()).thenReturn(Arrays.asList(game));

        mockMvc.perform(MockMvcRequestBuilders.get("/games")
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenDeleteByIdRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Game game = new Game();
        game.setId(1L);

        when(gameService.deleteById(1L)).thenReturn(game);

        mockMvc.perform(MockMvcRequestBuilders.delete("/games/{id}", game.getId())
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(game.getId()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    private Game createGame(Long id, Integer version, String title, String format, String genre, List<String> consoles, Integer numberOfPlayers, String collectionName, Integer releaseYear) {
        Game game = new Game();
        game.setId(id);
        game.setVersion(version);
        game.setTitle(title);
        game.setFormat(format);
        game.setGenre(genre);
        game.setConsoles(consoles);
        game.setNumberOfPlayers(numberOfPlayers);
        game.setCollectionName(collectionName);
        game.setReleaseYear(releaseYear);
        return game;
    }

    private Game createGame(String title, String format, String genre, List<String> consoles, Integer numberOfPlayers, String collectionName, Integer releaseYear) {
        Game game = new Game();
        game.setTitle(title);
        game.setFormat(format);
        game.setGenre(genre);
        game.setConsoles(consoles);
        game.setNumberOfPlayers(numberOfPlayers);
        game.setCollectionName(collectionName);
        game.setReleaseYear(releaseYear);
        return game;
    }
}