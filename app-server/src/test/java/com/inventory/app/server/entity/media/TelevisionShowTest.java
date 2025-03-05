//package com.inventory.app.server.entity.media;
//
//import com.inventory.app.server.entity.media.TelevisionShow;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//class TelevisionShowTest {
//    private TelevisionShow show1;
//    private TelevisionShow show2;
//
//    @BeforeEach
//    public void setUp() {
//        // Create two identical television shows
//        show1 = new TelevisionShow();
//        show1.setId(1L);
//        show1.setVersion(1);
//        show1.setTitle("Sample Show");
//        show1.setEpisodes(List.of("Writer1", "Writer2"));
//        show1.setSeason(1);
//        show1.setReleaseYear(2023);
//        show1.setGenre("Comedy");
//        show1.setFormat("DVD");
//        show1.setCollectionTitle("Jamie's Stuff");
//
//        show2 = new TelevisionShow();
//        show2.setId(1L);
//        show2.setVersion(1);
//        show2.setTitle("Sample Show");
//        show2.setEpisodes(List.of("Writer1", "Writer2"));
//        show2.setSeason(1);
//        show2.setReleaseYear(2023);
//        show2.setGenre("Comedy");
//        show2.setFormat("DVD");
//        show2.setCollectionTitle("Jamie's Stuff");
//    }
//
//    @AfterEach
//    public void tearDown() {
//        // Reset the objects to null
//        show1 = null;
//        show2 = null;
//    }
//
//    @Test
//    public void testEquals() {
//        // Test that equals method works correctly
//        assertEquals(show1, show2);
//    }
//
//    @Test
//    public void testNotEquals() {
//        // Modify one of the television shows
//        show2.setTitle("Modified Title");
//
//        // Test that not equals method works correctly
//        assertNotEquals(show1, show2);
//    }
//
//    @Test
//    public void testHashCode() {
//        // Test that hashCode method works correctly
//        assertThat(show1.hashCode()).isEqualTo(show2.hashCode());
//    }
//
//    @Test
//    public void testToString() {
//        // Test that toString method works correctly
//        String expectedToString = "TelevisionShow{writers=[Writer1, Writer2], " +
//                "season=1, " +
//                "releaseDate=2022-01-30, " +
//                "id=1, " +
//                "version=1, " +
//                "title='Sample Show', " +
//                "format=DVD, " +
//                "genre=Comedy, " +
//                "collectionName=Jamie's Stuff" +
//                "}";
//        assertEquals(expectedToString, show1.toString());
//    }
//
//    @Test
//    public void testGettersAndSetters() {
//        // Test getters and setters
//        assertThat(show1.getEpisodes()).containsExactly("Writer1", "Writer2");
//        assertEquals(1, show1.getSeason());
//        assertEquals(LocalDate.of(2022, 1, 30), show1.getReleaseYear());
//
//        // Modify values using setters
//        show1.setEpisodes(List.of("NewWriter"));
//        show1.setSeason(2);
//        show1.setReleaseYear(2023);
//
//        // Test that values have been updated
//        assertThat(show1.getEpisodes()).containsExactly("NewWriter");
//        assertEquals(2, show1.getSeason());
//        assertEquals(LocalDate.of(2023, 1, 30), show1.getReleaseYear());
//    }
//
//}