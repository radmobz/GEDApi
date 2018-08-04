package com.julien.juge.photos.api.utils.exception;

import io.vavr.collection.List;

public class InvalidInputException extends BusinessException {

    private final List<String> errors;

    public InvalidInputException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public InvalidInputException(List<String> messages) {
        super(messages.mkString(","));
        this.errors = messages;
    }

    public List<String> getErrors() {
        return errors;
    }
}
