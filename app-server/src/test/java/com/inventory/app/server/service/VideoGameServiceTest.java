package com.inventory.app.server.service;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.service.media.VideoGameService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class VideoGameServiceTest {
    @Mock
    private IBaseDao<Game> daoMock;

    @InjectMocks
    private VideoGameService underTest;
    private Game expectedGame;
    private Game expectedGame2;
    private SearchMediaRequest searchMediaRequest;
    String username;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        username = "jpompei";
        expectedGame = createGame(1L, 1, null, null, null, username);
        expectedGame2 = createGame(1L, 1, null, null, null, username);
        searchMediaRequest = buildSearchMediaRequest(null, null, null, null, username,  new ConcurrentHashMap<>());
    }

    @After
    public void tearDown() {
        // Reset mocks and clear any invocations
        reset(daoMock);
        expectedGame = null;
        searchMediaRequest = null;
    }

    @Test
    public void getAllGamesByCollectionTitle() {
        // GIVEN
        String collectionTitle = " Jamie's Stuff";
        expectedGame.setCollectionTitle(collectionTitle);
        List<Game> expectedGames = Arrays.asList(expectedGame, expectedGame);
        searchMediaRequest.setCollectionTitle(collectionTitle);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<Game> actualGames = underTest.searchGames(searchMediaRequest);
        //THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByNumberOfPlayers() {
        // GIVEN
        Integer numOfPlayers = 2;
        expectedGame.setNumberOfPlayers(numOfPlayers);
        List<Game> expectedGames = Arrays.asList(expectedGame, expectedGame);
        ConcurrentHashMap<String, Object> additionalAttributes= new ConcurrentHashMap<>();
        additionalAttributes.put("numberOfPlayers", numOfPlayers);
        searchMediaRequest.setAdditionalAttributes(additionalAttributes);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<Game> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByConsole() {
        // GIVEN
        List<String> consoles = Arrays.asList("Nintendo Switch");
        expectedGame.setConsoles(consoles);
        List<Game> expectedGames = Arrays.asList(expectedGame, expectedGame);
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey(), consoles);
        searchMediaRequest.setAdditionalAttributes(additionalAttributes);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<Game> actualGames = underTest.searchGames(searchMediaRequest);
        //THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByTitle() {
        // GIVEN
        String title = "It Takes Two";
        expectedGame.setTitle(title);
        List<Game> expectedGames = Arrays.asList(expectedGame, expectedGame);
        searchMediaRequest.setTitle(title);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<Game> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByGenre() {
        // GIVEN
        String genre = "Adventure";
        expectedGame.setGenre(genre);
        List<Game> expectedGames = Arrays.asList(expectedGame, expectedGame);
        searchMediaRequest.setGenre(genre);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<Game> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGames() {
        // GIVEN
        List<Game> expectedGames = Arrays.asList(expectedGame, expectedGame2);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<Game> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getGameById() {
        // WHEN
        when(daoMock.findOne(expectedGame.getId(),username)).thenReturn((expectedGame));
        Game actualGame = underTest.getById(expectedGame.getId(), username).get();
        // THEN
        assertEquals(expectedGame, actualGame);
    }

    @Test
    public void create(){
        // GIVEN
        String title = "It Takes Two";
        Game inputGame = expectedGame;
        inputGame.setTitle(title);
        // WHEN
        when(daoMock.findOne(1L, username)).thenReturn(null);
        when(daoMock.createOrUpdate(inputGame)).thenReturn(inputGame);
        Game savedGame = underTest.create(inputGame);
        // THEN
        assertNotNull(savedGame);
        assertEquals(inputGame.getTitle(), savedGame.getTitle());
    }

    @Test
    public void update(){
        // GIVEN
        Game existingGame = expectedGame;
        existingGame.setTitle("It Takes Two");
        Game gameToUpdate = expectedGame2;
        gameToUpdate.setTitle("Updated: It Takes Two");
        Game updatedGame = gameToUpdate;
        // WHEN
        when(daoMock.findOne(1L, username)).thenReturn((existingGame));
        when(daoMock.createOrUpdate(gameToUpdate)).thenReturn(updatedGame);
        Game result = underTest.update(gameToUpdate);
        // THEN
        assertNotNull(result);
        verify(daoMock, times(1)).createOrUpdate(gameToUpdate);
    }

    @Test
    public void deleteGameById(){
        // GIVEN
        Long gameId = 1L;
        Game gameToDelete = new Game();
        String username = "jpompei";
        // WHEN
        when(daoMock.findOne(gameId, username)).thenReturn(gameToDelete);
        Game result = underTest.deleteById(gameId, username);
        // THEN
        assertNotNull(result);
        verify(daoMock, times(1)).deleteById(gameId, username);
    }

    private Game createGame(Long id, Integer version, String title, String collectionTitle, Integer numberOfPlayers, String username){
        Game game = new Game();
        game.setId(id);
        game.setVersion(version);
        game.setTitle(title);
        game.setCollectionTitle(collectionTitle);
        game.setNumberOfPlayers(numberOfPlayers);
        game.setCreatedBy(username);
        return game;
    }

    private SearchMediaRequest buildSearchMediaRequest(String collectionTitle, String title, String genre, String format, String userName, ConcurrentHashMap<String, Object> additionalAttributes) {
        SearchMediaRequest searchMediaRequest = new SearchMediaRequest();
        searchMediaRequest.setCollectionTitle(collectionTitle);
        searchMediaRequest.setTitle(title);
        searchMediaRequest.setUsername(userName);
        searchMediaRequest.setGenre(genre);
        searchMediaRequest.setFormat(format);
        searchMediaRequest.setAdditionalAttributes(additionalAttributes);
        return searchMediaRequest;
    }
}