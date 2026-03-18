package com.inventory.app.server.entity.media;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GameTest {
    private VideoGame game1;
    private VideoGame game2;

    @Before
    public void setUp() {
        // Create two identical games
        game1 = new VideoGame();
        game1.setId(1L);
        game1.setTitle("Sample Game");
        game1.setConsoles(List.of("Console1", "Console2"));
        game1.setNumberOfPlayers(2);
        game1.setReleaseYear(2022);

        game2 = new VideoGame();
        game2.setId(1L);
        game2.setTitle("Sample Game");
        game2.setConsoles(List.of("Console1", "Console2"));
        game2.setNumberOfPlayers(2);
        game2.setReleaseYear(2022);
    }

    @After
    public void tearDown() {
        // Reset the objects to null
        game1 = null;
        game2 = null;
    }

    @Test
    public void testEquals() {
        assertEquals(game1, game2);
    }

    @Test
    public void testNotEquals() {
        // Modify one of the games
        game2.setTitle("Modified Title");

        // Test that not equals method works correctly
        assertNotEquals(game1, game2);
    }

    @Test
    public void testHashCode() {
        assertThat(game1.hashCode()).isEqualTo(game2.hashCode());
    }

    @Test
    public void testToString() {
        String expectedToString = "Game{consoles=[Console1, Console2], numberOfPlayers=2, releaseDate=2022 Media{id=1, title='Sample Game', genre='null', format='null', createdBy='null', createdOn=null, modifiedBy='null', modifiedOn=null, completed=null, onLoan=null, reviewRating=null, reviewDescription='null', collections=null, tags=null}}";
        assertEquals(expectedToString, game1.toString());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        assertThat(game1.getConsoles()).containsExactly("Console1", "Console2");
        assertEquals(2, game1.getNumberOfPlayers());
        assertEquals(2022, game1.getReleaseYear());

        // Modify values using setters
        game1.setConsoles(List.of("NewConsole"));
        game1.setNumberOfPlayers(4);
        game1.setReleaseYear(2023);

        // Test that values have been updated
        assertThat(game1.getConsoles()).containsExactly("NewConsole");
        assertEquals(4, game1.getNumberOfPlayers());
        assertEquals(2023, game1.getReleaseYear());
    }
}
