package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.entity.payload.request.MediaRequest;
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
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "consoles", qualifiedByName = "mapConsoles")
    @Mapping(source = "additionalAttributes", target = "numberOfPlayers", qualifiedByName = "mapNumberOfPlayers")
    @Mapping(source = "additionalAttributes", target = "releaseYear", qualifiedByName = "mapReleaseYear")
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

    @Named("mapReleaseYear")
    default Integer mapReleaseYear(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey()) ? (Integer) additionalAttributes.get(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey()) : null;
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
        if (game.getReleaseYear() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), game.getReleaseYear());
        }

        return additionalAttributes;
    }
}
