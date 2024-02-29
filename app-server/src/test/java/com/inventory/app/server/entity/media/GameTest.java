package com.inventory.app.server.entity.media;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GameTest {
    private Game game1;
    private Game game2;

    @BeforeEach
    public void setUp() {
        // Create two identical games
        game1 = new Game();
        game1.setId(1L);
        game1.setVersion(1);
        game1.setTitle("Sample Game");
        game1.setConsoles(List.of("Console1", "Console2"));
        game1.setNumberOfPlayers(2);
        game1.setReleaseYear(2022);

        game2 = new Game();
        game2.setId(1L);
        game2.setVersion(1);
        game2.setTitle("Sample Game");
        game2.setConsoles(List.of("Console1", "Console2"));
        game2.setNumberOfPlayers(2);
        game2.setReleaseYear(2022);
    }

    @AfterEach
    public void tearDown() {
        // Reset the objects to null
        game1 = null;
        game2 = null;
    }

    @Test
    public void testEquals() {
        // Test that equals method works correctly
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
        // Test that hashCode method works correctly
        assertThat(game1.hashCode()).isEqualTo(game2.hashCode());
    }

    @Test
    public void testToString() {
        // Test that toString method works correctly
        String expectedToString = "Game{consoles=[Console1, Console2], numberOfPlayers=2, " +
                "releaseDate=2022-01-30 " +
                "id=1, " +
                "version=1, " +
                "title='Sample Game', " +
                "format=" + null + ", " +
                "genre=" + null + ", " +
                "collectionName=" + null +
                "}";
        assertEquals(expectedToString, game1.toString());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        assertThat(game1.getConsoles()).containsExactly("Console1", "Console2");
        assertEquals(2, game1.getNumberOfPlayers());
        assertEquals(LocalDate.of(2022, 1, 30), game1.getReleaseYear());

        // Modify values using setters
        game1.setConsoles(List.of("NewConsole"));
        game1.setNumberOfPlayers(4);
        game1.setReleaseYear(2023);

        // Test that values have been updated
        assertThat(game1.getConsoles()).containsExactly("NewConsole");
        assertEquals(4, game1.getNumberOfPlayers());
        assertEquals(LocalDate.of(2023, 1, 30), game1.getReleaseYear());
    }
}
