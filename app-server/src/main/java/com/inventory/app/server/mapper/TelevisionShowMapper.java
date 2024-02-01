package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
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
public interface TelevisionShowMapper {

    TelevisionShowMapper INSTANCE = Mappers.getMapper(TelevisionShowMapper.class);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.version", target = "version")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "format")
    @Mapping(source = "mediaId.genre", target = "genre")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "writers", qualifiedByName = "mapWriters")
    @Mapping(source = "additionalAttributes", target = "season", qualifiedByName = "mapSeason")
    @Mapping(source = "additionalAttributes", target = "releaseDate", qualifiedByName = "mapReleaseDate")
    TelevisionShow mapMediaRequestToTelevisionShow(MediaRequest mediaRequest);


    @Named("mapWriters")
    default List<String> mapWriters(Map<String, Object> additionalAttributes) {
        Object writersValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.WRITERS.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> writersList = writersValue instanceof List<?> ? (List<String>) writersValue : Collections.emptyList();
        return writersList;
    }

    @Named("mapSeason")
    default Integer mapSeason(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.SEASON.getJsonKey()) ? (Integer) additionalAttributes.get(MediaInventoryAdditionalAttributes.SEASON.getJsonKey()) : null;
    }

    @Named("mapReleaseDate")
    default LocalDate mapReleaseDate(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey()) ? (LocalDate) additionalAttributes.get(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey()) : null;
    }

    default MediaResponse mapTelevisionShowToMediaResponseWithAdditionalAttributes(TelevisionShow televisionShow) {
        MediaResponse mediaResponse = mapTelevisionShowIdToMediaId(televisionShow);
        mediaResponse.setAdditionalAttributes(mapTelevisionShowToAdditionalAttributes(televisionShow));
        return mediaResponse;
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    MediaResponse mapTelevisionShowIdToMediaId(TelevisionShow televisionShow);

    @Named("mapGameToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapTelevisionShowToAdditionalAttributes(TelevisionShow televisionShow) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map writers if available
        //TODO should this be episodes?
        if (televisionShow.getWriters() != null && !televisionShow.getWriters().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.WRITERS.getJsonKey(), televisionShow.getWriters());
        }

        // Map season if available
        if (televisionShow.getSeason() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.SEASON.getJsonKey(), televisionShow.getSeason());
        }

        // Map release date if available
        if (televisionShow.getReleaseDate() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey(), televisionShow.getReleaseDate());
        }

        return additionalAttributes;
    }

}
