package com.inventory.app.server.entity.media;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MovieTest {

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    public void setUp() {
        // Create two identical movies
        movie1 = new Movie();
        movie1.setId(1L);
        movie1.setVersion(1);
        movie1.setTitle("Sample Movie");
        movie1.setDirectors(List.of("Director1", "Director2"));
        movie1.setReleaseDate(LocalDate.of(2022, 1, 30));
        movie1.setCollectionName("Jamie's Stuff");
        movie1.setGenre("Comedy");
        movie1.setFormat("DVD");

        movie2 = new Movie();
        movie2.setId(1L);
        movie2.setVersion(1);
        movie2.setTitle("Sample Movie");
        movie2.setDirectors(List.of("Director1", "Director2"));
        movie2.setReleaseDate(LocalDate.of(2022, 1, 30));
        movie2.setCollectionName("Jamie's Stuff");
        movie2.setGenre("Comedy");
        movie2.setFormat("DVD");
    }

    @AfterEach
    public void tearDown() {
        // Reset the objects to null
        movie1 = null;
        movie2 = null;
    }

    @Test
    public void testEquals() {
        // Test that equals method works correctly
        assertEquals(movie1, movie2);
    }

    @Test
    public void testNotEquals() {
        // Modify one of the movies
        movie2.setTitle("Modified Title");

        // Test that not equals method works correctly
        assertNotEquals(movie1, movie2);
    }

    @Test
    public void testHashCode() {
        // Test that hashCode method works correctly
        assertThat(movie1.hashCode()).isEqualTo(movie2.hashCode());
    }

    @Test
    public void testToString() {
        // Test that toString method works correctly
        String expectedToString = "Movie{directors=[Director1, Director2], " +
                "releaseDate=2022-01-30, " +
                "id=1, " +
                "version=1, " +
                "title='Sample Movie', " +
                "format=DVD, "+
                "genre=Comedy, " +
                "collectionName=Jamie's Stuff" +
                "}";
        assertEquals(expectedToString, movie1.toString());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        assertThat(movie1.getDirectors()).containsExactly("Director1", "Director2");
        assertEquals(LocalDate.of(2022, 1, 30), movie1.getReleaseDate());

        // Modify values using setters
        movie1.setDirectors(List.of("NewDirector"));
        movie1.setReleaseDate(LocalDate.of(2023, 1, 30));

        // Test that values have been updated
        assertThat(movie1.getDirectors()).containsExactly("NewDirector");
        assertEquals(LocalDate.of(2023, 1, 30), movie1.getReleaseDate());
    }

}