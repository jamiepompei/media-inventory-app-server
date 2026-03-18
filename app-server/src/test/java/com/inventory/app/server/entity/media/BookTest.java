package com.inventory.app.server.entity.media;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BookTest {

    private Book book1;
    private Book book2;

    @Before
    public void setUp() {
        // Create two identical books
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Sample Book");
        book1.setAuthors(List.of("Author1", "Author2"));
        book1.setCopyrightYear(2022);
        book1.setEdition(1);
        book1.setFormat("hardcover");
        book1.setGenre("Comedy");

        book2 = new Book();
        book2.setId(1L);
        book2.setTitle("Sample Book");
        book2.setAuthors(List.of("Author1", "Author2"));
        book2.setCopyrightYear(2022);
        book2.setEdition(1);
        book2.setFormat("hardcover");
        book2.setGenre("Comedy");
    }

    @After
    public void tearDown() {
        // Reset the objects to null
        book1 = null;
        book2 = null;
    }

    @Test
    public void testEquals() {
        assertEquals(book1, book2);
    }

    @Test
    public void testNotEquals() {
        // Modify one of the books
        book2.setTitle("Modified Title");

        // Test that not equals method works correctly
        assertNotEquals(book1, book2);
    }

    @Test
    public void testHashCode() {
        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
    }

    @Test
    public void testToString() {
        String expectedToString = "Book{authors=[Author1, Author2], copyrightYear=2022, edition=1 Media{id=1, title='Sample Book', genre='Comedy', format='hardcover', createdBy='null', createdOn=null, modifiedBy='null', modifiedOn=null, completed=null, onLoan=null, reviewRating=null, reviewDescription='null', collections=null, tags=null}}";
        assertEquals(expectedToString, book1.toString());
    }

    @Test
    public void testGettersAndSetters() {
        assertThat(book1.getAuthors()).containsExactly("Author1", "Author2");
        assertEquals(2022, book1.getCopyrightYear());
        assertEquals(1, book1.getEdition());

        // Modify values using setters
        book1.setAuthors(List.of("NewAuthor"));
        book1.setCopyrightYear(2023);
        book1.setEdition(2);

        // Test that values have been updated
        assertThat(book1.getAuthors()).containsExactly("NewAuthor");
        assertEquals(2023, book1.getCopyrightYear());
        assertEquals(2, book1.getEdition());
    }
}
