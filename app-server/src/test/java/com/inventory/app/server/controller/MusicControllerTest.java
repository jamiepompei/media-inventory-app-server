package com.inventory.app.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.service.media.MusicService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MusicController.class)
@AutoConfigureMockMvc
public class MusicControllerTest {

    @MockBean
    private MusicService musicService;

    @Autowired
    private MusicController underTest;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenTelevisionShowControllerInjected_thenNotNull() {
        assertThat(underTest).isNotNull();
    }

    @Test
    public void whenPostRequestMusicAndValidMusic_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Music mockMusic = createMusic("Eye On The Bat", "Vinyl", "Rock", Arrays.asList("Palehound"), Arrays.asList("Good Sex", "The Clutch", "Eye On The Bat", "Independence Day", "Route 22", "Right About You", "You Want It You Got It", "Fadin"), "Jamie's Stuff", 2023);

        when(musicService.create(any())).thenReturn(mockMusic);

        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey(), mockMusic.getArtists());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey(), mockMusic.getSongList());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMusic.getReleaseYear());
        MediaRequest mediaRequest = MediaRequest.builder()
                .title(mockMusic.getTitle())
                .genre(mockMusic.getGenre())
                .format(mockMusic.getFormat())
                .collectionName(mockMusic.getCollectionName())
                .additionalAttributes(additionalAttributes)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String music = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/music")
                .content(music)
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMusic.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMusic.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMusic.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMusic.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.artists").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.songList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMusic.getReleaseYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPostRequestMusicAndInvalidMusic_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String music = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/music")
                .content(music)
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPutRequestMusicAndValidMusic_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Music mockMusic = createMusic(1L, 1,"Eye On The Bat", "Vinyl", "Rock", Arrays.asList("Palehound"), Arrays.asList("Good Sex", "The Clutch", "Eye On The Bat", "Independence Day", "Route 22", "Right About You", "You Want It You Got It", "Fadin"), "Jamie's Stuff", 2023);

        when(musicService.create(any())).thenReturn(mockMusic);

        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey(), mockMusic.getArtists());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey(), mockMusic.getSongList());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMusic.getReleaseYear());
        MediaRequest mediaRequest = MediaRequest.builder()
                .id(mockMusic.getId())
                .version(mockMusic.getVersion())
                .title(mockMusic.getTitle())
                .genre(mockMusic.getGenre())
                .format(mockMusic.getFormat())
                .collectionName(mockMusic.getCollectionName())
                .additionalAttributes(additionalAttributes)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String music = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/music/{id}", mediaRequest.getId())
                        .content(music)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMusic.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMusic.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMusic.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMusic.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.artists").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.songList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMusic.getReleaseYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPutRequestMusicAndInvalidMusic_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();
        mediaRequest.setId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String music = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/music/{id}", mediaRequest.getId())
                        .content(music)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenGetAllMusicRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Music music = new Music();

        when(musicService.getAll()).thenReturn(Arrays.asList(music));

        mockMvc.perform(MockMvcRequestBuilders.get("/music")
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenDeleteByIdRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Music music = new Music();
        music.setId(1L);

        when(musicService.deleteById(music.getId())).thenReturn(music);

        mockMvc.perform(MockMvcRequestBuilders.delete("/music/{id}", music.getId())
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(music.getId()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    private Music createMusic(Long id, Integer version, String title, String format, String genre, List<String> artists, List<String> songList, String collectionName, Integer releaseYear) {
        Music music = new Music();
        music.setId(id);
        music.setVersion(version);
        music.setTitle(title);
        music.setFormat(format);
        music.setGenre(genre);
        music.setCollectionName(collectionName);
        music.setArtists(artists);
        music.setSongList(songList);
        music.setReleaseYear(releaseYear);
        return music;
    }

    private Music createMusic(String title, String format, String genre, List<String> artists, List<String> songList, String collectionName, Integer releaseYear) {
        Music music = new Music();
        music.setTitle(title);
        music.setFormat(format);
        music.setGenre(genre);
        music.setCollectionName(collectionName);
        music.setArtists(artists);
        music.setSongList(songList);
        music.setReleaseYear(releaseYear);
        return music;
    }
}