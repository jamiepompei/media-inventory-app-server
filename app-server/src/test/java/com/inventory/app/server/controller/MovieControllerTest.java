package com.inventory.app.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.service.media.MovieService;
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
@WebMvcTest(controllers = MovieController.class)
@AutoConfigureMockMvc
public class MovieControllerTest {

    @MockBean
    private MovieService movieService;

    @Autowired
    private MovieController underTest;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenMovieControllerInjected_thenNotNull() {
        assertThat(underTest).isNotNull();
    }

    @Test
    public void whenPostRequestMovieAndValidMovie_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Movie mockMovie = createMovie("The Goonies", "DVD", "Adventure", Arrays.asList("Jessie Pinkman"), "Jamie's Stuff", 2023);

        when(movieService.create(any())).thenReturn(mockMovie);

        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), mockMovie.getDirectors());
        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMovie.getReleaseYear());
        MediaRequest mediaRequest = MediaRequest.builder()
                .title(mockMovie.getTitle())
                .genre(mockMovie.getGenre())
                .format(mockMovie.getFormat())
                .collectionName(mockMovie.getCollectionName())
                .additionalAttributes(additionalAttributes)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String movie = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .content(movie)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMovie.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMovie.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMovie.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMovie.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.artists").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.songList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMovie.getReleaseYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPostRequestMusicAndInvalidMovie_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String movie = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .content(movie)
                        .contentType(jsonMediaType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    private Movie createMovie(Long id, Integer version, String title, String format, String genre, List<String> directors, String collectionName, Integer releaseYear) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setVersion(version);
        movie.setTitle(title);
        movie.setFormat(format);
        movie.setGenre(genre);
        movie.setCollectionName(collectionName);
        movie.setDirectors(directors);
        movie.setReleaseYear(releaseYear);
        return movie;
    }

    private Movie createMovie(String title, String format, String genre, List<String> directors, String collectionName, Integer releaseYear) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setFormat(format);
        movie.setGenre(genre);
        movie.setCollectionName(collectionName);
        movie.setDirectors(directors);
        movie.setReleaseYear(releaseYear);
        return movie;
    }
}