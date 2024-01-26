package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Media;
import com.inventory.app.server.entity.media.Music;
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
public interface MusicMapper {

    MusicMapper INSTANCE = Mappers.getMapper(MusicMapper.class);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.version", target = "version")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "format")
    @Mapping(source = "mediaId.genre", target = "genre")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "artists", qualifiedByName = "mapArtists")
    @Mapping(source = "additionalAttributes", target = "songList", qualifiedByName = "mapSongs")
    @Mapping(source = "additionalAttributes", target = "releaseDate", qualifiedByName = "mapReleaseDate")
    Music mapMediaRequestToMusic(MediaRequest mediaRequest);

    @Named("mapArtists")
    default List<String> mapArtists(Map<String, Object> additionalAttributes) {
        Object artistsValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> artistsList = artistsValue instanceof List<?> ? (List<String>) artistsValue : Collections.emptyList();
        return artistsList;
    }

    @Named("mapSongs")
    default List<String> mapSongs(Map<String, Object> additionalAttributes) {
        Object songListValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> songList = songListValue instanceof List<?> ? (List<String>) songListValue : Collections.emptyList();
        return songList;
    }

    @Named("mapReleaseDate")
    default LocalDate mapReleaseDate(Map<String, Object> additionalAttributes) {
        return (LocalDate) additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey(), null);
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    MediaId mapMusicIdToMediaId(Media entity);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.version", target = "version")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "format")
    @Mapping(source = "mediaId.genre", target = "genre")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "artists", qualifiedByName = "mapAuthors")
    @Mapping(source = "additionalAttributes", target = "songList", qualifiedByName = "mapCopyrightYear")
    @Mapping(source = "additionalAttributes", target = "releaseDate", qualifiedByName = "mapEdition")
    Music mapMediaRequestToMedia(MediaRequest mediaRequest);

    default MediaResponse mapMusicToMediaResponseWithAdditionalAttributes(Music music){
        MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setMediaId(mapMusicIdToMediaId(music));
        mediaResponse.setAdditionalAttributes(mapMusicToAdditionalAttributes(music));
        return mediaResponse;
    };

    @Named("mapMusicToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapMusicToAdditionalAttributes(Music music) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map authors if available
        if (music.getArtists() != null && !music.getArtists().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.ARTISTS.getJsonKey(), music.getArtists());
        }

        // Map edition if available
        if (music.getSongList() != null && !music.getSongList().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.SONG_LIST.getJsonKey(), music.getSongList());
        }

        // Map copyright year if available
        if (music.getReleaseDate() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.RELEASE_DATE.getJsonKey(), music.getReleaseDate());
        }

        return additionalAttributes;
    }
}
