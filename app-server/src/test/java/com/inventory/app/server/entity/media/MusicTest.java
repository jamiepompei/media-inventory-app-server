//package com.inventory.app.server.entity.media;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//public class MusicTest {
//    private Music music1;
//    private Music music2;
//
//    @Before
//    public void setUp() {
//        // Create two identical music objects
//        music1 = new Music();
//        music1.setId(1L);
//        music1.setVersion(1);
//        music1.setTitle("Sample Music");
//        music1.setArtists(List.of("Artist1", "Artist2"));
//        music1.setSongList(List.of("Song1", "Song2"));
//        music1.setReleaseYear( 2022);
//        music1.setGenre("Rock");
//        music1.setFormat("Vinyl");
//        music1.setCollectionTitle("Jamie's Stuff");
//
//        music2 = new Music();
//        music2.setId(1L);
//        music2.setVersion(1);
//        music2.setTitle("Sample Music");
//        music2.setArtists(List.of("Artist1", "Artist2"));
//        music2.setSongList(List.of("Song1", "Song2"));
//        music2.setReleaseYear(2022);
//        music2.setGenre("Rock");
//        music2.setFormat("Vinyl");
//        music2.setCollectionTitle("Jamie's Stuff");
//    }
//
//    @After
//    public void tearDown() {
//        // Reset the objects to null
//        music1 = null;
//        music2 = null;
//    }
//
//    @Test
//    public void testEquals() {
//        // Test that equals method works correctly
//        assertEquals(music1, music2);
//    }
//
//    @Test
//    public void testNotEquals() {
//        // Modify one of the music objects
//        music2.setTitle("Modified Title");
//
//        // Test that not equals method works correctly
//        assertNotEquals(music1, music2);
//    }
//
//    @Test
//    public void testHashCode() {
//        // Test that hashCode method works correctly
//        assertThat(music1.hashCode()).isEqualTo(music2.hashCode());
//    }
//
//    @Test
//    public void testToString() {
//        // Test that toString method works correctly
//        String expectedToString = "Music{artists=[Artist1, Artist2], " +
//                "songList=[Song1, Song2], " +
//                "releaseYear=2022, " +
//                "id=1, " +
//                "version=1, " +
//                "title='Sample Music', " +
//                "format=Vinyl, " +
//                "genre=Rock, " +
//                "collectionName=Jamie's Stuff" +
//                "}";
//        assertEquals(expectedToString, music1.toString());
//    }
//
//    @Test
//    public void testGettersAndSetters() {
//        // Test getters and setters
//        assertThat(music1.getArtists()).containsExactly("Artist1", "Artist2");
//        assertThat(music1.getSongList()).containsExactly("Song1", "Song2");
//        assertEquals(2022, music1.getReleaseYear());
//
//        // Modify values using setters
//        music1.setArtists(List.of("NewArtist"));
//        music1.setSongList(List.of("NewSong"));
//        music1.setReleaseYear(2023);
//
//        // Test that values have been updated
//        assertThat(music1.getArtists()).containsExactly("NewArtist");
//        assertThat(music1.getSongList()).containsExactly("NewSong");
//        assertEquals(2023, music1.getReleaseYear());
//    }
//
//}