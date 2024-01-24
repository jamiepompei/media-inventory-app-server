package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.entity.payload.request.MediaId;
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
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.version", target = "version")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "format")
    @Mapping(source = "mediaId.genre", target = "genre")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes", target = "authors", qualifiedByName = "mapAuthors")
    @Mapping(source = "additionalAttributes", target = "copyrightYear", qualifiedByName = "mapCopyrightYear")
    @Mapping(source = "additionalAttributes", target = "edition", qualifiedByName = "mapEdition")
    Book mapMediaRequestToBook(MediaRequest mediaRequest);


    @Named("mapAuthors")
    default List<String> mapAuthors(Map<String, Object> additionalAttributes) {
        Object authorsValue = additionalAttributes.getOrDefault(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> authorsList = authorsValue instanceof List<?> ? (List<String>) authorsValue : Collections.emptyList();
        return authorsList;
    }

    @Named("mapCopyrightYear")
    default Integer mapCopyrightYear(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey()) ? (int) additionalAttributes.get(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey()) : null;
    }

    @Named("mapEdition")
    default Integer mapEdition(Map<String, Object> additionalAttributes) {
        return additionalAttributes.containsKey(MediaInventoryAdditionalAttributes.EDITION.getJsonKey()) ? (int) additionalAttributes.get(MediaInventoryAdditionalAttributes.EDITION.getJsonKey()) : null;
    }

    default MediaResponse mapBookToMediaResponseWithAdditionalAttributes(Book book) {
        MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setMediaId(mapBookIdToMediaId(book));
        mediaResponse.setAdditionalAttributes(mapBookToAdditionalAttributes(book));
        return mediaResponse;
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "collectionName", target = "collectionName")
    MediaId mapBookIdToMediaId(Book book);

    @Named("mapBookToAdditionalAttributes")
    default ConcurrentHashMap<String, Object> mapBookToAdditionalAttributes(Book book) {
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();

        // Map authors if available
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), book.getAuthors());
        }

        // Map edition if available
        if (book.getEdition() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.EDITION.getJsonKey(), book.getEdition());
        }

        // Map copyright year if available
        if (book.getCopyrightYear() != null) {
            additionalAttributes.put(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey(), book.getCopyrightYear());
        }

        return additionalAttributes;
    }
}

