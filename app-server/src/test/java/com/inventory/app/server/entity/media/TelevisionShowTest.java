package com.inventory.app.server.entity.media;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TelevisionShowTest {
    private TelevisionShow show1;
    private TelevisionShow show2;

    @Before
    public void setUp() {
        // Create two identical television shows
        show1 = new TelevisionShow();
        show1.setId(1L);
        show1.setTitle("Sample Show");
        show1.setEpisodes(List.of("Writer1", "Writer2"));
        show1.setSeason(1);
        show1.setReleaseYear(2022);
        show1.setGenre("Comedy");
        show1.setFormat("DVD");

        show2 = new TelevisionShow();
        show2.setId(1L);
        show2.setTitle("Sample Show");
        show2.setEpisodes(List.of("Writer1", "Writer2"));
        show2.setSeason(1);
        show2.setReleaseYear(2022);
        show2.setGenre("Comedy");
        show2.setFormat("DVD");
    }

    @After
    public void tearDown() {
        // Reset the objects to null
        show1 = null;
        show2 = null;
    }

    @Test
    public void testEquals() {
        assertEquals(show1, show2);
    }

    @Test
    public void testNotEquals() {
        // Modify one of the television shows
        show2.setTitle("Modified Title");

        // Test that not equals method works correctly
        assertNotEquals(show1, show2);
    }

    @Test
    public void testHashCode() {
        assertThat(show1.hashCode()).isEqualTo(show2.hashCode());
    }

    @Test
    public void testToString() {
        String expectedToString = "TelevisionShow{episodes=[Writer1, Writer2], season=1, releaseDate=2022, Media{id=1, title='Sample Show', genre='Comedy', format='DVD', createdBy='null', createdOn=null, modifiedBy='null', modifiedOn=null, completed=null, onLoan=null, reviewRating=null, reviewDescription='null', collections=null, tags=null}}";
        assertEquals(expectedToString, show1.toString());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        assertThat(show1.getEpisodes()).containsExactly("Writer1", "Writer2");
        assertEquals(1, show1.getSeason());
        assertEquals(2022, show1.getReleaseYear());

        // Modify values using setters
        show1.setEpisodes(List.of("NewWriter"));
        show1.setSeason(2);
        show1.setReleaseYear(2023);

        // Test that values have been updated
        assertThat(show1.getEpisodes()).containsExactly("NewWriter");
        assertEquals(2, show1.getSeason());
        assertEquals(2023, show1.getReleaseYear());
    }

}