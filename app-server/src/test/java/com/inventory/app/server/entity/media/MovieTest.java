package com.inventory.app.server.entity.media;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MovieTest {

    private Movie movie1;
    private Movie movie2;

    @Before
    public void setUp() {
        // Create two identical movies
        movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Sample Movie");
        movie1.setDirectors(List.of("Director1", "Director2"));
        movie1.setReleaseYear(2022);
        movie1.setGenre("Comedy");
        movie1.setFormat("DVD");

        movie2 = new Movie();
        movie2.setId(1L);
        movie2.setTitle("Sample Movie");
        movie2.setDirectors(List.of("Director1", "Director2"));
        movie2.setReleaseYear(2022);
        movie2.setGenre("Comedy");
        movie2.setFormat("DVD");
    }

    @After
    public void tearDown() {
        // Reset the objects to null
        movie1 = null;
        movie2 = null;
    }

    @Test
    public void testEquals() {
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
        assertThat(movie1.hashCode()).isEqualTo(movie2.hashCode());
    }

    @Test
    public void testToString() {
        String expectedToString = "Movie{directors=[Director1, Director2], releaseYear=2022, Media{id=1, title='Sample Movie', genre='Comedy', format='DVD', createdBy='null', createdOn=null, modifiedBy='null', modifiedOn=null, completed=null, onLoan=null, reviewRating=null, reviewDescription='null', collections=null, tags=null}}";
        assertEquals(expectedToString, movie1.toString());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        assertThat(movie1.getDirectors()).containsExactly("Director1", "Director2");
        assertEquals(2022, movie1.getReleaseYear());

        // Modify values using setters
        movie1.setDirectors(List.of("NewDirector"));
        movie1.setReleaseYear(2023);

        // Test that values have been updated
        assertThat(movie1.getDirectors()).containsExactly("NewDirector");
        assertEquals(2023, movie1.getReleaseYear());
    }
}