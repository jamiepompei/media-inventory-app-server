package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Game;
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
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.version", target = "version")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "format")
    @Mapping(source = "mediaId.genre", target = "genre")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "consoles", qualifiedByName = "mapConsoles")
    @Mapping(source = "additionalAttributes", target = "numberOfPlayers", qualifiedByName = "mapNumberOfPlayers")
    @Mapping(source = "additionalAttributes", target = "releaseDate", qualifiedByName = "mapReleaseDate")
    Game mapMediaRequestToGame(MediaRequest mediaRequest);


    @Named("mapConsoles")
    default List<String> mapConsoles(Map<String, Object> additionalAttributes) {
        Object consolesValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> consoleList = consolesValue instanceof List<?> ? (List<String>) consolesValue : Collections.emptyList();
        return consoleList;
    }

    @Named("mapNumberOfPlayers")
    default Integer mapNumberOfPlayers(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.NUMBER_OF_PLAYERS.getJsonKey()) ? (Integer) additionalAttributes.get(MediaInventoryAdditionalAttributes.NUMBER_OF_PLAYERS.getJsonKey()) : null;
    }

    @Named("mapReleaseDate")
    default LocalDate mapReleaseDate(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey()) ? (LocalDate) additionalAttributes.get(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey()) : null;
    }

    default MediaResponse mapGameToMediaResponseWithAdditionalAttributes(Game game) {
        MediaResponse mediaResponse = mapGameIdToMediaId(game);
        mediaResponse.setAdditionalAttributes(mapGameToAdditionalAttributes(game));
        return mediaResponse;
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    MediaResponse mapGameIdToMediaId(Game game);

    @Named("mapGameToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapGameToAdditionalAttributes(Game game) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map authors if available
        if (game.getConsoles() != null && !game.getConsoles().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.CONSOLES.getJsonKey(), game.getConsoles());
        }

        // Map edition if available
        if (game.getNumberOfPlayers() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.NUMBER_OF_PLAYERS.getJsonKey(), game.getNumberOfPlayers());
        }

        // Map copyright year if available
        if (game.getReleaseDate() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey(), game.getReleaseDate());
        }

        return additionalAttributes;
    }
}
