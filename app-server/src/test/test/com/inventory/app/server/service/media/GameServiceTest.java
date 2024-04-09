package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.repository.IBaseDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    @Mock
    private IBaseDao<Game> daoMock;

    @InjectMocks
    private GameService underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() {
        // Reset mocks and clear any invocations
        reset(daoMock);
    }

    @Test
    public void getAllGamesByCollectionTitle() {
        // GIVEN
        String collectionTitle = " Jamie's Stuff";
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("collection_name", collectionTitle, username)).thenReturn(expectedGames);
        List<Game> actualGames = underTest.getAllGamesByCollectionTitle(collectionTitle, username);
        //THEN
        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("collection_name", collectionTitle, username);
    }

    @Test
    public void getAllGamesByNumberOfPlayers() {
        // GIVEN
        Integer numOfPlayers = 2;
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("number_of_players", numOfPlayers, username)).thenReturn(expectedGames);
        List<Game> actualGames = underTest.getAllGamesByNumberOfPlayers(numOfPlayers, username);
        // THEN
        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("number_of_players", numOfPlayers, username);
    }

    @Test
    public void getAllGamesByConsole() {
        // GIVEN
        List<String> consoles = Arrays.asList("Nintendo Switch");
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("consoles", consoles, username)).thenReturn(expectedGames);
        List<Game> actualGames = underTest.getAllGamesByConsole(consoles, username);
        //THEN
        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("consoles", consoles, username);
    }

    @Test
    public void getAllGamesByTitle() {
        // GIVEN
        String title = "It Takes Two";
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("title", title, username)).thenReturn(expectedGames);
        List<Game> actualGames = underTest.getAllGamesByTitle(title, username);
        // THEN
        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("title", title, username);
    }

    @Test
    public void getAllGamesByGenre() {
        // GIVEN
        String genre = "Adventure";
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("genre", genre, username)).thenReturn(expectedGames);
        List<Game> actualGames = underTest.getAllGamesByGenre(genre, username);
        // THEN
        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("genre", genre, username);
    }

    @Test
    public void getAllGames() {
        // GIVEN
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());
        String username = "jpompei";
        // WHEN
        when(daoMock.findAllByUsername(username)).thenReturn(expectedGames);
        List<Game> actualGames = underTest.getAllByUsername(username);
        // THEN
        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void getGameById() {
        // GIVEN
        Long gameId = 1L;
        Game expectedGame = new Game();
        String username = "jpompei";
        // WHEN
        when(daoMock.findOne(gameId, username)).thenReturn(expectedGame);
        Game actualGame = underTest.getById(gameId, username);
        // THEN
        assertEquals(expectedGame, actualGame);
        verify(daoMock, times(1)).findOne(gameId, username);
    }

    @Test
    public void create(){
        // GIVEN
        String title = "It Takes Two";
        Game inputGame = createGame(1L, 1, title);
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("title", title, username)).thenReturn(Collections.emptyList());
        when(daoMock.createOrUpdate(inputGame)).thenReturn(inputGame);
        Game savedGame = underTest.create(inputGame, username);
        // THEN
        assertNotNull(savedGame);
        assertEquals(inputGame.getTitle(), savedGame.getTitle());
    }

    @Test
    public void update(){
        // GIVEN
        Integer expectedVersion = 2;
        Game existingGame = createGame(1L, 1, "It Takes Two");
        Game updatedGame = createGame(1L, 1, "Updated It Takes Two");
        String username = "jpompei";
        // WHEN
        when(daoMock.findOne(updatedGame.getId(), username)).thenReturn(existingGame);
        Game result = underTest.update(updatedGame, username);
        // THEN
        assertNotNull(result);
        assertEquals(updatedGame, result);
        assertEquals(expectedVersion, result.getVersion());
        verify(daoMock, times(1)).createOrUpdate(updatedGame);
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

    private Game createGame(Long id, Integer version, String title){
        Game game = new Game();
        game.setId(id);
        game.setVersion(version);
        game.setTitle(title);
        return game;
    }
}