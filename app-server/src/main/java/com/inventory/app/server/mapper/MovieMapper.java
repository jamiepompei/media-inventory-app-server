package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.MediaId;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mapper
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.version", target = "version")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "format")
    @Mapping(source = "mediaId.genre", target = "genre")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "directors", qualifiedByName = "mapDirectors")
    @Mapping(source = "additionalAttributes", target = "releaseDate", qualifiedByName = "mapReleaseDate")
    Movie mapMediaRequestToMovie(MediaRequest mediaRequest);


    @Named("mapDirectors")
    default List<String> mapDirectors(Map<String, Object> additionalAttributes) {
        Object directorsValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> directorsList = directorsValue instanceof List<?> ? (List<String>) directorsValue : Collections.emptyList();
        return directorsList;
    }

    @Named("mapReleaseDate")
    default LocalDate mapReleaseDate(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey()) ? (LocalDate) additionalAttributes.get(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey()) : null;
    }

    default MediaResponse mapMovieToMediaResponseWithAdditionalAttributes(Movie movie) {
        MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setMediaId(mapMovieIdToMediaId(movie));
        mediaResponse.setAdditionalAttributes(mapMovieToAdditionalAttributes(movie));
        return mediaResponse;
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    MediaId mapMovieIdToMediaId(Movie movie);

    @Named("mapBookToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapMovieToAdditionalAttributes(Movie movie) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map directors if available
        if (movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), movie.getDirectors());
        }

        // Map release date if available
        if (movie.getReleaseDate() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey(), movie.getReleaseDate());
        }

        return additionalAttributes;
    }
}
