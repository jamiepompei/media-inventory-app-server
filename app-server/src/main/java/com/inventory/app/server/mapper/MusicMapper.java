package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Media;
import com.inventory.app.server.entity.media.Music;
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
public interface MusicMapper {

    MusicMapper INSTANCE = Mappers.getMapper(MusicMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "artists", qualifiedByName = "mapArtists")
    @Mapping(source = "additionalAttributes", target = "songList", qualifiedByName = "mapSongList")
    @Mapping(source = "additionalAttributes", target = "releaseYear", qualifiedByName = "mapReleaseYear")
    Music mapMediaRequestToMusic(MediaRequest mediaRequest);

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
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    MediaResponse mapMusicIdToMediaId(Media entity);


    default MediaResponse mapMusicToMediaResponseWithAdditionalAttributes(Music music){
        MediaResponse mediaResponse = mapMusicIdToMediaId(music);
        mediaResponse.setAdditionalAttributes(mapMusicToAdditionalAttributes(music));
        return mediaResponse;
    }

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
