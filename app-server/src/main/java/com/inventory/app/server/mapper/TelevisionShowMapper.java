package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
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
public interface TelevisionShowMapper {

    TelevisionShowMapper INSTANCE = Mappers.getMapper(TelevisionShowMapper.class);

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
    @Mapping(source = "additionalAttributes", target = "episodes", qualifiedByName = "mapEpisodes")
    @Mapping(source = "additionalAttributes", target = "season", qualifiedByName = "mapSeason")
    @Mapping(source = "additionalAttributes", target = "releaseYear", qualifiedByName = "mapReleaseYear")
    TelevisionShow mapMediaRequestToTelevisionShow(UpdateCreateMediaRequest mediaRequest);


    @Named("mapEpisodes")
    default List<String> mapEpisodes(Map<String, Object> additionalAttributes) {
        Object episodesValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> episdoesList = episodesValue instanceof List<?> ? (List<String>) episodesValue : Collections.emptyList();
        return episdoesList;
    }

    @Named("mapSeason")
    default Integer mapSeason(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.SEASON.getJsonKey()) ? (Integer) additionalAttributes.get(MediaInventoryAdditionalAttributes.SEASON.getJsonKey()) : null;
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
    @Mapping(source = "televisionShow", target = "additionalAttributes", qualifiedByName = "mapTelevisionShowToAdditionalAttributes")
    MediaResponse mapTelevisionShowToMediaResponse(TelevisionShow televisionShow);

    @Named("mapTelevisionShowToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapTelevisionShowToAdditionalAttributes(TelevisionShow televisionShow) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map episodes if available
        if (televisionShow.getEpisodes() != null && !televisionShow.getEpisodes().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), televisionShow.getEpisodes());
        }

        // Map season if available
        if (televisionShow.getSeason() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.SEASON.getJsonKey(), televisionShow.getSeason());
        }

        // Map release date if available
        if (televisionShow.getReleaseYear() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), televisionShow.getReleaseYear());
        }

        return additionalAttributes;
    }
}
