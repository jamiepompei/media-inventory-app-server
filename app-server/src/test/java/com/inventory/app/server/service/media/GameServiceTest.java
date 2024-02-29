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
        String collectionTitle = " Jamie's Stuff";
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());

        when(daoMock.findByField("collection_name", collectionTitle)).thenReturn(expectedGames);

        List<Game> actualGames = underTest.getAllGamesByCollectionTitle(collectionTitle);

        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("collection_name", collectionTitle);
    }

    @Test
    public void getAllGamesByNumberOfPlayers() {
        Integer numOfPlayers = 2;
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());

        when(daoMock.findByField("number_of_players", numOfPlayers)).thenReturn(expectedGames);

        List<Game> actualGames = underTest.getAllGamesByNumberOfPlayers(numOfPlayers);

        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("number_of_players", numOfPlayers);
    }

    @Test
    public void getAllGamesByConsole() {
        List<String> consoles = Arrays.asList("Nintendo Switch");
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());

        when(daoMock.findByField("consoles", consoles)).thenReturn(expectedGames);

        List<Game> actualGames = underTest.getAllGamesByConsole(consoles);

        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("consoles", consoles);
    }

    @Test
    public void getAllGamesByTitle() {
        String title = "It Takes Two";
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());

        when(daoMock.findByField("title", title)).thenReturn(expectedGames);

        List<Game> actualGames = underTest.getAllGamesByTitle(title);

        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("title", title);
    }

    @Test
    public void getAllGamesByGenre() {
        String genre = "Adventure";
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());

        when(daoMock.findByField("genre", genre)).thenReturn(expectedGames);

        List<Game> actualGames = underTest.getAllGamesByGenre(genre);

        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findByField("genre", genre);
    }

    @Test
    public void getAllGames() {
        List<Game> expectedGames = Arrays.asList(new Game(), new Game());

        when(daoMock.findAll()).thenReturn(expectedGames);

        List<Game> actualGames = underTest.getAll();

        assertEquals(expectedGames, actualGames);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void getGameById() {
        Long gameId = 1L;
        Game expectedGame = new Game();

        when(daoMock.findOne(gameId)).thenReturn(expectedGame);

        Game actualGame = underTest.getById(gameId);

        assertEquals(expectedGame, actualGame);
        verify(daoMock, times(1)).findOne(gameId);
    }

    @Test
    public void create(){
        String title = "It Takes Two";
        Game inputGame = new Game();
        inputGame.setTitle(title);

        when(daoMock.findByField("title", title)).thenReturn(Collections.emptyList());
        when(daoMock.createOrUpdate(inputGame)).thenReturn(inputGame);

        Game savedGame = underTest.create(inputGame);

        assertNotNull(savedGame);
        assertEquals(inputGame.getTitle(), savedGame.getTitle());
    }

    @Test
    public void update(){
        Integer expectedVersion = 2;
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setVersion(1);
        existingGame.setTitle("It Takes Two");

        Game updatedGame = new Game();
        updatedGame.setId(1L);
        updatedGame.setVersion(1);
        updatedGame.setTitle("Updated It Takes Two");

        when(daoMock.findOne(updatedGame.getId())).thenReturn(existingGame);
        Game result = underTest.update(updatedGame);

        assertNotNull(result);
        assertEquals(updatedGame, result);
        assertEquals(expectedVersion, result.getVersion());

        verify(daoMock, times(1)).createOrUpdate(updatedGame);
    }

    @Test
    public void deleteGameById(){
        Long gameId = 1L;
        Game gameToDelete = new Game();

        when(daoMock.findOne(gameId)).thenReturn(gameToDelete);

        Game result = underTest.deleteById(gameId);

        assertNotNull(result);
        verify(daoMock, times(1)).deleteById(gameId);
    }
}