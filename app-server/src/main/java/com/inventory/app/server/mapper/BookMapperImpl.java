package com.inventory.app.server.mapper;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.entity.payload.MediaRequest;

import java.util.Collections;
import java.util.List;

public class BookMapperImpl implements BookMapper {

    public static final String AUTHOR_LIST_DELIMITER = ",";

    @Override
    public Book mapMediaRequestToBook(MediaRequest mediaRequest) {
        if (mediaRequest == null) {
            return null; // TODO throw exception but ideally should not happen
        }

        Book book = new Book();
        book.setId(mediaRequest.getMediaId().getId());
        book.setFormat(mediaRequest.getMediaId().getFormat());
        book.setGenre(mediaRequest.getMediaId().getGenre());
        book.setEdition((Integer) mediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.EDITION.getJsonKey()));
        book.setCopyrightYear((Integer) mediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey()));

        // Custom mapping for authors
        book.setAuthors(mapAuthors(mediaRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey())));

        return book;
    }

    @Override
        public List<String> mapAuthors(Object authors) {
            if (authors == null) {
                return Collections.emptyList(); // TODO throw exception
            }
            List<String> result;
            if (authors instanceof List<?>) {
                // Authors are already a List<String>
                @SuppressWarnings("unchecked")
                List<String> stringAuthors = (List<String>) authors;
                result = stringAuthors;
            } else if (authors instanceof String) {
                // Authors are a single string, split by a delimiter (e.g., comma)
                result = List.of(((String) authors).split(AUTHOR_LIST_DELIMITER));
            } else {
                throw new IllegalArgumentException("Unsupported authors format: " + authors.getClass());
            }
            return result;
        }
}
