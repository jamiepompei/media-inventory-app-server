package com.inventory.app.server.mapper;

import com.inventory.app.server.config.converter.StringListConverter;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = StringListConverter.class)
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "mediaId.id", target = "id")
    @Mapping(source = "mediaId.title", target = "title")
    @Mapping(source = "mediaId.format", target = "")
    @Mapping(source = "mediaId.genre", target = "")
    @Mapping(source = "mediaId.collectionName", target = "collectionName")
    @Mapping(source = "additionalAttributes.authors", target = "authors", qualifiedByName = "stringListConverter")
    @Mapping(source = "additionalAttributes.copyrightYear", target = "copyrightYear")
    @Mapping(source = "additionalAttributes.edition", target = "edition")
    Book mapMediaRequestToBook(MediaRequest mediaRequest);

    @Mapping(source = "id", target = "mediaId.id")
    @Mapping(source = "title", target = "mediaId.title")
    @Mapping(source = "format", target = "mediaId.format")
    @Mapping(source = "genre", target = "mediaId.genre")
    @Mapping(source = "collectionName", target = "mediaId.collectionName")
    @Mapping(source = "authors", target = "additionalAttributes.authors", qualifiedByName = "stringListConverter")
    @Mapping(source = "edition", target = "additionalAttributes.edition")
    @Mapping(source = "copyrightYear", target = "additionalAttributes.copyrightYear")
    Book mapBookToMediaResponse(Book book);

}
