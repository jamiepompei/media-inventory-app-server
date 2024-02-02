package com.inventory.app.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.service.media.TelevisionService;
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

        ConcurrentHashMap<String, Object> additionalBookAttributes = new ConcurrentHashMap<>();
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.WRITERS.getJsonKey(), mockTelevisionShow.getWriters());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockTelevisionShow.getReleaseYear());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.SEASON.getJsonKey(), mockTelevisionShow.getSeason());
        MediaRequest mediaRequest = MediaRequest.builder()
                .title(mockTelevisionShow.getTitle())
                .genre(mockTelevisionShow.getGenre())
                .format(mockTelevisionShow.getFormat())
                .collectionName(mockTelevisionShow.getCollectionName())
                .additionalAttributes(additionalBookAttributes)
                .build();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String televisionShow = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/televisionShows")
                        .content(televisionShow)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockTelevisionShow.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockTelevisionShow.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockTelevisionShow.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockTelevisionShow.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.writers").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockTelevisionShow.getReleaseYear().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.season").value(mockTelevisionShow.getSeason()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    private TelevisionShow createTelevisionShow(Long id, Integer version, String title, String format, String genre, Integer season, List<String> writers, String collectionName, Integer releaseYear) {
        TelevisionShow show = new TelevisionShow();
        show.setId(id);
        show.setVersion(version);
        show.setTitle(title);
        show.setFormat(format);
        show.setGenre(genre);
        show.setCollectionName(collectionName);
        show.setSeason(season);
        show.setWriters(writers);
        show.setReleaseYear(releaseYear);
        return show;
    }

    private TelevisionShow createTelevisionShow(String title, String format, String genre, Integer season, List<String> writers, String collectionName, Integer releaseYear) {
        TelevisionShow show = new TelevisionShow();
        show.setTitle(title);
        show.setFormat(format);
        show.setGenre(genre);
        show.setCollectionName(collectionName);
        show.setSeason(season);
        show.setWriters(writers);
        show.setReleaseYear(releaseYear);
        return show;
    }
}