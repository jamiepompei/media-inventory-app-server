package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Music;
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
public interface MusicMapper {

    MusicMapper INSTANCE = Mappers.getMapper(MusicMapper.class);

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
    @Mapping(source = "additionalAttributes", target = "artists", qualifiedByName = "mapArtists")
    @Mapping(source = "additionalAttributes", target = "songList", qualifiedByName = "mapSongList")
    @Mapping(source = "additionalAttributes", target = "releaseYear", qualifiedByName = "mapReleaseYear")
    Music mapMediaRequestToMusic(UpdateCreateMediaRequest mediaRequest);

    @Named("mapArtists")
    default List<String> mapArtists(Map<String, Object> additionalAttributes) {
        Object artistsValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> artistsList = artistsValue instanceof List<?> ? (List<String>) artistsValue : Collections.emptyList();
        return artistsList;
    }

    @Named("mapSongList")
    default List<String> mapSongList(Map<String, Object> additionalAttributes) {
        Object songListValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> songList = songListValue instanceof List<?> ? (List<String>) songListValue : Collections.emptyList();
        return songList;
    }

    @Named("mapReleaseYear")
    default Integer mapReleaseYear(Map<String, Object> additionalAttributes) {
        return (Integer) additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), null);
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
    @Mapping(source = "music", target = "additionalAttributes", qualifiedByName = "mapMusicToAdditionalAttributes")
    MediaResponse mapMusicToMediaResponse(Music music);

    @Named("mapMusicToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapMusicToAdditionalAttributes(Music music) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map artists if available
        if (music.getArtists() != null && !music.getArtists().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey(), music.getArtists());
        }

        // Map song list if available
        if (music.getSongList() != null && !music.getSongList().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey(), music.getSongList());
        }

        // Map release date if available
        if (music.getReleaseYear() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_YEAR.getJsonKey(), music.getReleaseYear());
        }

        return additionalAttributes;
    }
}
