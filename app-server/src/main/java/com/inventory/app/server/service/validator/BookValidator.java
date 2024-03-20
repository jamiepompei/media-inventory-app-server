package com.inventory.app.server.service.validator;

import com.inventory.app.server.entity.media.Book;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BookValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        if (checkInputString(book.getTitle())) {
            errors.rejectValue("title", "title.empty");
        }
        if (checkInputString(book.getFormat())) {
            errors.rejectValue("format", "format.empty");
        }
        if (checkInputString(book.getGenre())) {
            errors.rejectValue("genre", "genre.empty");
        }
        if (checkInputString(book.getCollectionName())) {
            errors.rejectValue("collectionName", "collectionName.empty");
        }
    }
    private boolean checkInputString(String input) {
        return (input == null || input.trim().isEmpty());
    }
}
