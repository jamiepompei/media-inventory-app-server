package com.inventory.app.server.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_MODIFIED)
public class NoChangesToUpdateException extends RuntimeException {

    public NoChangesToUpdateException(String message) {
        super(message);
    }
}
