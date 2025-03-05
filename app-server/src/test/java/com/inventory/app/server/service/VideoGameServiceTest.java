package com.inventory.app.server.service;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.VideoGame;
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
    private IBaseDao<VideoGame> daoMock;

    @InjectMocks
    private VideoGameService underTest;
    private VideoGame expectedGame;
    private VideoGame expectedGame2;
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
        List<VideoGame> expectedGames = Arrays.asList(expectedGame, expectedGame);
        searchMediaRequest.setCollectionTitle(collectionTitle);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<VideoGame> actualGames = underTest.searchGames(searchMediaRequest);
        //THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByNumberOfPlayers() {
        // GIVEN
        Integer numOfPlayers = 2;
        expectedGame.setNumberOfPlayers(numOfPlayers);
        List<VideoGame> expectedGames = Arrays.asList(expectedGame, expectedGame);
        ConcurrentHashMap<String, Object> additionalAttributes= new ConcurrentHashMap<>();
        additionalAttributes.put("numberOfPlayers", numOfPlayers);
        searchMediaRequest.setAdditionalAttributes(additionalAttributes);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<VideoGame> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByConsole() {
        // GIVEN
        List<String> consoles = Arrays.asList("Nintendo Switch");
        expectedGame.setConsoles(consoles);
        List<VideoGame> expectedGames = Arrays.asList(expectedGame, expectedGame);
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey(), consoles);
        searchMediaRequest.setAdditionalAttributes(additionalAttributes);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<VideoGame> actualGames = underTest.searchGames(searchMediaRequest);
        //THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByTitle() {
        // GIVEN
        String title = "It Takes Two";
        expectedGame.setTitle(title);
        List<VideoGame> expectedGames = Arrays.asList(expectedGame, expectedGame);
        searchMediaRequest.setTitle(title);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<VideoGame> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGamesByGenre() {
        // GIVEN
        String genre = "Adventure";
        expectedGame.setGenre(genre);
        List<VideoGame> expectedGames = Arrays.asList(expectedGame, expectedGame);
        searchMediaRequest.setGenre(genre);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<VideoGame> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getAllGames() {
        // GIVEN
        List<VideoGame> expectedGames = Arrays.asList(expectedGame, expectedGame2);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedGames);
        List<VideoGame> actualGames = underTest.searchGames(searchMediaRequest);
        // THEN
        assertEquals(expectedGames, actualGames);
    }

    @Test
    public void getGameById() {
        // WHEN
        when(daoMock.findOne(expectedGame.getId(),username)).thenReturn((expectedGame));
        VideoGame actualGame = underTest.getById(expectedGame.getId(), username).get();
        // THEN
        assertEquals(expectedGame, actualGame);
    }

    @Test
    public void create(){
        // GIVEN
        String title = "It Takes Two";
        VideoGame inputGame = expectedGame;
        inputGame.setTitle(title);
        // WHEN
        when(daoMock.findOne(1L, username)).thenReturn(null);
        when(daoMock.createOrUpdate(inputGame)).thenReturn(inputGame);
        VideoGame savedGame = underTest.create(inputGame);
        // THEN
        assertNotNull(savedGame);
        assertEquals(inputGame.getTitle(), savedGame.getTitle());
    }

    @Test
    public void update(){
        // GIVEN
        VideoGame existingGame = expectedGame;
        existingGame.setTitle("It Takes Two");
        VideoGame gameToUpdate = expectedGame2;
        gameToUpdate.setTitle("Updated: It Takes Two");
        VideoGame updatedGame = gameToUpdate;
        // WHEN
        when(daoMock.findOne(1L, username)).thenReturn((existingGame));
        when(daoMock.createOrUpdate(gameToUpdate)).thenReturn(updatedGame);
        VideoGame result = underTest.update(gameToUpdate);
        // THEN
        assertNotNull(result);
        verify(daoMock, times(1)).createOrUpdate(gameToUpdate);
    }

    @Test
    public void deleteGameById(){
        // GIVEN
        Long gameId = 1L;
        VideoGame gameToDelete = new VideoGame();
        String username = "jpompei";
        // WHEN
        when(daoMock.findOne(gameId, username)).thenReturn(gameToDelete);
        VideoGame result = underTest.deleteById(gameId, username);
        // THEN
        assertNotNull(result);
        verify(daoMock, times(1)).deleteById(gameId, username);
    }

    private VideoGame createGame(Long id, Integer version, String title, String collectionTitle, Integer numberOfPlayers, String username){
        VideoGame game = new VideoGame();
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