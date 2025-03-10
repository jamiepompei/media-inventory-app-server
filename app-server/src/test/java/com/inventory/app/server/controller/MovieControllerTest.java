//
//package com.inventory.app.server.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
//import com.inventory.app.server.controller.media.MovieController;
//import com.inventory.app.server.entity.media.Movie;
//import com.inventory.app.server.entity.payload.request.MediaRequest;
//import com.inventory.app.server.service.media.MovieService;
//import org.hamcrest.core.Is;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = MovieController.class)
//@AutoConfigureMockMvc
//public class MovieControllerTest {
//
//    @MockBean
//    private MovieService movieService;
//
//    @Autowired
//    private MovieController underTest;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void whenMovieControllerInjected_thenNotNull() {
//        assertThat(underTest).isNotNull();
//    }
//
//    @Test
//    public void whenPostRequestMovieAndValidMovie_thenCorrectResponse() throws Exception {
//        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
//
//        Movie mockMovie = createMovie("The Goonies", "DVD", "Adventure", Arrays.asList("Jessie Pinkman"), "Jamie's Stuff", 2023);
//
//        when(movieService.create(any(), any())).thenReturn(mockMovie);
//
//        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
//        additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), mockMovie.getDirectors());
//        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMovie.getReleaseYear());
//        MediaRequest mediaRequest = MediaRequest.builder()
//                .title(mockMovie.getTitle())
//                .genre(mockMovie.getGenre())
//                .format(mockMovie.getFormat())
//                .collectionName(mockMovie.getCollectionName())
//                .additionalAttributes(additionalAttributes)
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String movie = objectMapper.writeValueAsString(mediaRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
//                        .content(movie)
//                        .contentType(jsonMediaType))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMovie.getTitle()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMovie.getGenre()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMovie.getFormat()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMovie.getCollectionName()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.directors").isArray())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMovie.getReleaseYear()))
//                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
//    }
//
//    @Test
//    public void whenPostRequestMusicAndInvalidMovie_thenCorrectResponse() throws Exception {
//        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
//
//        MediaRequest mediaRequest = new MediaRequest();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String movie = objectMapper.writeValueAsString(mediaRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
//                        .content(movie)
//                        .contentType(jsonMediaType))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
//                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
//    }
//
//    @Test
//    public void whenPutRequestMovieAndValidMovie_thenCorrectResponse() throws Exception {
//        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
//
//        Movie mockMovie = createMovie(1L, 1, "The Goonies", "DVD", "Adventure", Arrays.asList("Jessie Pinkman"), "Jamie's Stuff", 2023);
//        String username = "jpompei";
//
//        when(movieService.update(mockMovie, username)).thenReturn(mockMovie);
//
//        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
//        additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), mockMovie.getDirectors());
//        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMovie.getReleaseYear());
//        MediaRequest mediaRequest = MediaRequest.builder()
//                .id(mockMovie.getId())
//                .version(mockMovie.getVersion())
//                .title(mockMovie.getTitle())
//                .genre(mockMovie.getGenre())
//                .format(mockMovie.getFormat())
//                .collectionName(mockMovie.getCollectionName())
//                .additionalAttributes(additionalAttributes)
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String movie = objectMapper.writeValueAsString(mediaRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/movies/{id}", mockMovie.getId())
//                .content(movie)
//                .contentType(jsonMediaType))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMovie.getTitle()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMovie.getGenre()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMovie.getFormat()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMovie.getCollectionName()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.directors").isArray())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMovie.getReleaseYear()))
//                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
//    }
//
//    @Test
//    public void whenPutRequestMovieAndInvalidMovie_thenCorrectResponse() throws Exception {
//        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
//
//        MediaRequest mediaRequest = new MediaRequest();
//        mediaRequest.setId(1L);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String movie = objectMapper.writeValueAsString(mediaRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/movies/{id}", mediaRequest.getId())
//                        .content(movie)
//                        .contentType(jsonMediaType))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
//                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
//    }
//
//    @Test
//    public void whenGetAllMoviesRequestValid_thenCorrectResponse() throws Exception {
//        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
//
//        Movie movie = new Movie();
//        String username = "jpompei";
//
//        when(movieService.getAllByUsername(username)).thenReturn(Arrays.asList(movie));
//
//        mockMvc.perform((MockMvcRequestBuilders.get("/movies"))
//                .contentType(jsonMediaType))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
//                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
//    }
//
//    @Test
//    public void whenDeleteByIdRequestValid_thenCorrectResponse() throws Exception {
//        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
//
//        Movie movie = new Movie();
//        movie.setId(1L);
//        String username = "jpompei";
//
//        when(movieService.deleteById(movie.getId(), username)).thenReturn(movie);
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/movie/{id}", movie.getId())
//                        .contentType(jsonMediaType))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(movie.getId()))
//                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
//    }
//
//    private Movie createMovie(Long id, Integer version, String title, String format, String genre, List<String> directors, String collectionName, Integer releaseYear) {
//        Movie movie = new Movie();
//        movie.setId(id);
//        movie.setVersion(version);
//        movie.setTitle(title);
//        movie.setFormat(format);
//        movie.setGenre(genre);
//        movie.setCollectionName(collectionName);
//        movie.setDirectors(directors);
//        movie.setReleaseYear(releaseYear);
//        return movie;
//    }
//
//    private Movie createMovie(String title, String format, String genre, List<String> directors, String collectionName, Integer releaseYear) {
//        Movie movie = new Movie();
//        movie.setTitle(title);
//        movie.setFormat(format);
//        movie.setGenre(genre);
//        movie.setCollectionName(collectionName);
//        movie.setDirectors(directors);
//        movie.setReleaseYear(releaseYear);
//        return movie;
//    }
//}
//
////package com.inventory.app.server.controller;
////
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
////import com.inventory.app.server.controller.media.MovieController;
////import com.inventory.app.server.entity.media.Movie;
////import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
////import com.inventory.app.server.service.media.MovieService;
////import org.hamcrest.core.Is;
////import org.junit.Test;
////import org.junit.runner.RunWith;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
////import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////import org.springframework.boot.test.mock.mockito.MockBean;
////import org.springframework.http.MediaType;
////import org.springframework.test.context.junit4.SpringRunner;
////import org.springframework.test.web.servlet.MockMvc;
////import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
////import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
////
////import java.util.Arrays;
////import java.util.List;
////import java.util.concurrent.ConcurrentHashMap;
////
////import static org.assertj.core.api.Assertions.assertThat;
////import static org.mockito.ArgumentMatchers.any;
////import static org.mockito.Mockito.when;
////
////@RunWith(SpringRunner.class)
////@WebMvcTest(controllers = MovieController.class)
////@AutoConfigureMockMvc
////public class MovieControllerTest {
////
////    @MockBean
////    private MovieService movieService;
////
////    @Autowired
////    private MovieController underTest;
////
////    @Autowired
////    private MockMvc mockMvc;
////
////    @Test
////    public void whenMovieControllerInjected_thenNotNull() {
////        assertThat(underTest).isNotNull();
////    }
////
////    @Test
////    public void whenPostRequestMovieAndValidMovie_thenCorrectResponse() throws Exception {
////        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
////
////        Movie mockMovie = createMovie("The Goonies", "DVD", "Adventure", Arrays.asList("Jessie Pinkman"), "Jamie's Stuff", 2023);
////
////        when(movieService.create(any(), any())).thenReturn(mockMovie);
////
////        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
////        additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), mockMovie.getDirectors());
////        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMovie.getReleaseYear());
////        UpdateCreateMediaRequest mediaRequest = UpdateCreateMediaRequest.builder()
////                .title(mockMovie.getTitle())
////                .genre(mockMovie.getGenre())
////                .format(mockMovie.getFormat())
////                .collectionTitle(mockMovie.getCollectionTitle())
////                .additionalAttributes(additionalAttributes)
////                .build();
////
////        ObjectMapper objectMapper = new ObjectMapper();
////        String movie = objectMapper.writeValueAsString(mediaRequest);
////
////        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
////                        .content(movie)
////                        .contentType(jsonMediaType))
////                .andExpect(MockMvcResultMatchers.status().isCreated())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMovie.getTitle()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMovie.getGenre()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMovie.getFormat()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMovie.getCollectionTitle()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.directors").isArray())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMovie.getReleaseYear()))
////                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
////    }
////
////    @Test
////    public void whenPostRequestMusicAndInvalidMovie_thenCorrectResponse() throws Exception {
////        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
////
////        UpdateCreateMediaRequest mediaRequest = new UpdateCreateMediaRequest();
////
////        ObjectMapper objectMapper = new ObjectMapper();
////        String movie = objectMapper.writeValueAsString(mediaRequest);
////
////        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
////                        .content(movie)
////                        .contentType(jsonMediaType))
////                .andExpect(MockMvcResultMatchers.status().isBadRequest())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
////                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
////    }
////
////    @Test
////    public void whenPutRequestMovieAndValidMovie_thenCorrectResponse() throws Exception {
////        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
////
////        Movie mockMovie = createMovie(1L, 1, "The Goonies", "DVD", "Adventure", Arrays.asList("Jessie Pinkman"), "Jamie's Stuff", 2023);
////        String username = "jpompei";
////
////        when(movieService.update(mockMovie, username)).thenReturn(mockMovie);
////
////        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
////        additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), mockMovie.getDirectors());
////        additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), mockMovie.getReleaseYear());
////        UpdateCreateMediaRequest mediaRequest = UpdateCreateMediaRequest.builder()
////                .id(mockMovie.getId())
////                .version(mockMovie.getVersion())
////                .title(mockMovie.getTitle())
////                .genre(mockMovie.getGenre())
////                .format(mockMovie.getFormat())
////                .collectionTitle(mockMovie.getCollectionTitle())
////                .additionalAttributes(additionalAttributes)
////                .build();
////
////        ObjectMapper objectMapper = new ObjectMapper();
////        String movie = objectMapper.writeValueAsString(mediaRequest);
////
////        mockMvc.perform(MockMvcRequestBuilders.put("/movies/{id}", mockMovie.getId())
////                .content(movie)
////                .contentType(jsonMediaType))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockMovie.getTitle()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockMovie.getGenre()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockMovie.getFormat()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockMovie.getCollectionTitle()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.directors").isArray())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.releaseYear").value(mockMovie.getReleaseYear()))
////                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
////    }
////
////    @Test
////    public void whenPutRequestMovieAndInvalidMovie_thenCorrectResponse() throws Exception {
////        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
////
////        UpdateCreateMediaRequest mediaRequest = new UpdateCreateMediaRequest();
////        mediaRequest.setId(1L);
////
////        ObjectMapper objectMapper = new ObjectMapper();
////        String movie = objectMapper.writeValueAsString(mediaRequest);
////
////        mockMvc.perform(MockMvcRequestBuilders.put("/movies/{id}", mediaRequest.getId())
////                        .content(movie)
////                        .contentType(jsonMediaType))
////                .andExpect(MockMvcResultMatchers.status().isBadRequest())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
////                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
////    }
////
////    @Test
////    public void whenGetAllMoviesRequestValid_thenCorrectResponse() throws Exception {
////        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
////
////        Movie movie = new Movie();
////        String username = "jpompei";
////
////        when(movieService.getAllByUsername(username)).thenReturn(Arrays.asList(movie));
////
////        mockMvc.perform((MockMvcRequestBuilders.get("/movies"))
////                .contentType(jsonMediaType))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
////                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
////    }
////
////    @Test
////    public void whenDeleteByIdRequestValid_thenCorrectResponse() throws Exception {
////        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);
////
////        Movie movie = new Movie();
////        movie.setId(1L);
////        String username = "jpompei";
////
////        when(movieService.deleteById(movie.getId(), username)).thenReturn(movie);
////
////        mockMvc.perform(MockMvcRequestBuilders.delete("/movie/{id}", movie.getId())
////                        .contentType(jsonMediaType))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(movie.getId()))
////                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
////    }
////
////    private Movie createMovie(Long id, Integer version, String title, String format, String genre, List<String> directors, String collectionName, Integer releaseYear) {
////        Movie movie = new Movie();
////        movie.setId(id);
////        movie.setVersion(version);
////        movie.setTitle(title);
////        movie.setFormat(format);
////        movie.setGenre(genre);
////        movie.setCollectionTitle(collectionName);
////        movie.setDirectors(directors);
////        movie.setReleaseYear(releaseYear);
////        return movie;
////    }
////
////    private Movie createMovie(String title, String format, String genre, List<String> directors, String collectionName, Integer releaseYear) {
////        Movie movie = new Movie();
////        movie.setTitle(title);
////        movie.setFormat(format);
////        movie.setGenre(genre);
////        movie.setCollectionTitle(collectionName);
////        movie.setDirectors(directors);
////        movie.setReleaseYear(releaseYear);
////        return movie;
////    }
////}
//
