package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Movie;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mapper
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "username", target = "createdBy")
    @Mapping(source = "username", target = "modifiedBy")
    @Mapping(source = "completed", target = "completed")
    @Mapping(source = "onLoan", target = "onLoan")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "reviewRating", target = "reviewRating")
    @Mapping(source = "reviewDescription", target = "reviewDescription")
    @Mapping(source = "additionalAttributes", target = "directors", qualifiedByName = "mapDirectors")
    @Mapping(source = "additionalAttributes", target = "releaseYear", qualifiedByName = "mapReleaseYear")
    Movie mapMediaRequestToMovie(UpdateCreateMediaRequest mediaRequest);


    @Named("mapDirectors")
    default List<String> mapDirectors(Map<String, Object> additionalAttributes) {
        Object directorsValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> directorsList = directorsValue instanceof List<?> ? (List<String>) directorsValue : Collections.emptyList();
        return directorsList;
    }

    @Named("mapReleaseYear")
    default Integer mapReleaseYear(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey()) ? (Integer) additionalAttributes.get(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey()) : null;
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "createdBy", target = "username")
    @Mapping(source = "createdOn", target = "createdOn")
    @Mapping(source = "modifiedBy", target = "modifiedBy")
    @Mapping(source = "modifiedOn", target = "modifiedOn")
    @Mapping(source = "completed", target = "completed")
    @Mapping(source = "onLoan", target = "onLoan")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "reviewRating", target = "reviewRating")
    @Mapping(source = "reviewDescription", target = "reviewDescription")
    @Mapping(source = "movie", target = "additionalAttributes", qualifiedByName = "mapMovieToAdditionalAttributes")
    MediaResponse mapMovieToMediaResponse(Movie movie);

    @Named("mapMovieToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapMovieToAdditionalAttributes(Movie movie) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map directors if available
        if (movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.DIRECTORS.getJsonKey(), movie.getDirectors());
        }

        // Map release date if available
        if (movie.getReleaseYear() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), movie.getReleaseYear());
        }

        return additionalAttributes;
    }
}
