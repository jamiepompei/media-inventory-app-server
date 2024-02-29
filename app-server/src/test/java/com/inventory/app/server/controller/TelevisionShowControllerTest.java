package com.inventory.app.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.service.media.TelevisionService;
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
@WebMvcTest(controllers = TelevisionShowController.class)
@AutoConfigureMockMvc
public class TelevisionShowControllerTest {

    @MockBean
    private TelevisionService televisionService;

    @Autowired
    private TelevisionShowController underTest;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenTelevisionShowControllerInjected_thenNotNull() { assertThat(underTest).isNotNull();}

    @Test
    public void whenPostRequestTelevisionShowAndValidTelevisionShow_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        TelevisionShow mockTelevisionShow = createTelevisionShow("Test", "DVD", "Comedy", 1, Arrays.asList("Jon Snow, Jessie Pinkman"), "Jamie's Stuff", 2023);

        when(televisionService.create(any())).thenReturn(mockTelevisionShow);

        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), mockTelevisionShow.getEpisodes());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockTelevisionShow.getReleaseYear());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.SEASON.getJsonKey(), mockTelevisionShow.getSeason());
        MediaRequest mediaRequest = MediaRequest.builder()
                .title(mockTelevisionShow.getTitle())
                .genre(mockTelevisionShow.getGenre())
                .format(mockTelevisionShow.getFormat())
                .collectionName(mockTelevisionShow.getCollectionName())
                .additionalAttributes(additionalAttributes)
                .build();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String televisionShow = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/televisionShows")
                        .content(televisionShow)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockTelevisionShow.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockTelevisionShow.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockTelevisionShow.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockTelevisionShow.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.episodes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockTelevisionShow.getReleaseYear().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.season").value(mockTelevisionShow.getSeason()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPostRequestTelevisionShowAndInvalidTelevisionShow_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String televisionShow = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/televisionShows")
                        .content(televisionShow)
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
    public void whenPutRequestTelevisionShowAndValidTelevisionShow_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        TelevisionShow mockTelevisionShow = createTelevisionShow(1L, 1,"Test", "DVD", "Comedy", 1, Arrays.asList("Jon Snow, Jessie Pinkman"), "Jamie's Stuff", 2023);

        when(televisionService.update(any())).thenReturn(mockTelevisionShow);

        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), mockTelevisionShow.getEpisodes());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockTelevisionShow.getReleaseYear());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.SEASON.getJsonKey(), mockTelevisionShow.getSeason());
        MediaRequest mediaRequest = MediaRequest.builder()
                .id(mockTelevisionShow.getId())
                .version(mockTelevisionShow.getVersion())
                .title(mockTelevisionShow.getTitle())
                .genre(mockTelevisionShow.getGenre())
                .format(mockTelevisionShow.getFormat())
                .collectionName(mockTelevisionShow.getCollectionName())
                .additionalAttributes(additionalAttributes)
                .build();

        // Convert MediaRequest to JSON String
        ObjectMapper objectMapper = new ObjectMapper();
        String televisionShow = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/televisionShows/{id}", mediaRequest.getId())
                        .content(televisionShow)
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockTelevisionShow.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockTelevisionShow.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockTelevisionShow.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockTelevisionShow.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.episodes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockTelevisionShow.getReleaseYear().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.season").value(mockTelevisionShow.getSeason()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPutRequestTelevisionShowAndInvalidTelevisionShow_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();
        mediaRequest.setId(1L);

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String televisionShow = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/televisionShows/{id}", mediaRequest.getId())
                        .content(televisionShow)
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
    public void whenGetByEpisodesRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        List<String> episodes = Arrays.asList("Jessie Pinkman takes over", "Jon Snow wins the battle");
        TelevisionShow televisionShow = new TelevisionShow();

        when(televisionService.getAllTelevisionShowsByEpisode(episodes)).thenReturn(Arrays.asList(televisionShow));

        mockMvc.perform(MockMvcRequestBuilders.get("/televisionShows/{episodes}", String.join(",", episodes))
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenGetAllBooksRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        TelevisionShow televisionShow = new TelevisionShow();

        when(televisionService.getAll()).thenReturn(Arrays.asList(televisionShow));

        mockMvc.perform(MockMvcRequestBuilders.get("/televisionShows")
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenDeleteByIdRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        TelevisionShow televisionShow = new TelevisionShow();
        televisionShow.setId(1L);

        when(televisionService.deleteById(televisionShow.getId())).thenReturn(televisionShow);

        mockMvc.perform(MockMvcRequestBuilders.delete("/televisionShows/{id}", televisionShow.getId())
                .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(televisionShow.getId()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    private TelevisionShow createTelevisionShow(Long id, Integer version, String title, String format, String genre, Integer season, List<String> episodes, String collectionName, Integer releaseYear) {
        TelevisionShow show = new TelevisionShow();
        show.setId(id);
        show.setVersion(version);
        show.setTitle(title);
        show.setFormat(format);
        show.setGenre(genre);
        show.setCollectionName(collectionName);
        show.setSeason(season);
        show.setEpisodes(episodes);
        show.setReleaseYear(releaseYear);
        return show;
    }

    private TelevisionShow createTelevisionShow(String title, String format, String genre, Integer season, List<String> episodes, String collectionName, Integer releaseYear) {
        TelevisionShow show = new TelevisionShow();
        show.setTitle(title);
        show.setFormat(format);
        show.setGenre(genre);
        show.setCollectionName(collectionName);
        show.setSeason(season);
        show.setEpisodes(episodes);
        show.setReleaseYear(releaseYear);
        return show;
    }
}