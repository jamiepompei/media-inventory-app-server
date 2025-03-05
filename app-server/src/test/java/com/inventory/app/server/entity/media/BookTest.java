//package com.inventory.app.server.entity.media;
//
//import com.inventory.app.server.entity.media.Book;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//public class BookTest {
//
//    private Book book1;
//    private Book book2;
//
//    @BeforeEach
//    public void setUp() {
//        // Create two identical books
//        book1 = new Book();
//        book1.setId(1L);
//        book1.setVersion(1);
//        book1.setTitle("Sample Book");
//        book1.setAuthors(List.of("Author1", "Author2"));
//        book1.setCopyrightYear(2022);
//        book1.setEdition(1);
//        book1.setCollectionTitle("Jamie's Stuff");
//        book1.setFormat("hardcover");
//        book1.setGenre("Comedy");
//
//        book2 = new Book();
//        book2.setId(1L);
//        book2.setVersion(1);
//        book2.setTitle("Sample Book");
//        book2.setAuthors(List.of("Author1", "Author2"));
//        book2.setCopyrightYear(2022);
//        book2.setEdition(1);
//        book2.setCollectionTitle("Jamie's Stuff");
//        book2.setFormat("hardcover");
//        book2.setGenre("Comedy");
//    }
//
//    @AfterEach
//    public void tearDown() {
//        // Reset the objects to null
//        book1 = null;
//        book2 = null;
//    }
//
//    @Test
//    public void testEquals() {
//        // Test that equals method works correctly
//        assertEquals(book1, book2);
//    }
//
//    @Test
//    public void testNotEquals() {
//        // Modify one of the books
//        book2.setTitle("Modified Title");
//
//        // Test that not equals method works correctly
//        assertNotEquals(book1, book2);
//    }
//
//    @Test
//    public void testHashCode() {
//        // Test that hashCode method works correctly
//        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
//    }
//
//    @Test
//    public void testToString() {
//        // Test that toString method works correctly
//        String expectedToString = "Book{authors=[Author1, Author2], copyrightYear=2022, edition=1 " +
//                "id=1, version=1, title='Sample Book', format=hardcover, genre=Comedy, collectionName=Jamie's Stuff}";
//        assertEquals(expectedToString, book1.toString());
//    }
//
//    @Test
//    public void testGettersAndSetters() {
//        // Test getters and setters
//        assertThat(book1.getAuthors()).containsExactly("Author1", "Author2");
//        assertEquals(2022, book1.getCopyrightYear());
//        assertEquals(1, book1.getEdition());
//
//        // Modify values using setters
//        book1.setAuthors(List.of("NewAuthor"));
//        book1.setCopyrightYear(2023);
//        book1.setEdition(2);
//
//        // Test that values have been updated
//        assertThat(book1.getAuthors()).containsExactly("NewAuthor");
//        assertEquals(2023, book1.getCopyrightYear());
//        assertEquals(2, book1.getEdition());
//    }
//}
