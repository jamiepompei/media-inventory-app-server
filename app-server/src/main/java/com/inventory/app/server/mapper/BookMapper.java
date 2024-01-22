package com.inventory.app.server.mapper;

import com.inventory.app.server.entity.Book;
import com.inventory.app.server.entity.payload.MediaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "authors", qualifiedByName = "mapAuthors")
    Book mapMediaRequestToBook(MediaRequest mediaRequest);

    List<String> mapAuthors(Object authors);
}
