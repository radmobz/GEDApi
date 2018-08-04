package com.julien.juge.photos.api.utils.exception;

import java.util.List;

/**
 * Created by adelegue on 19/10/2016.
 */
public class ValidationException extends RuntimeException {

    private final List<Throwable> errors;

    public ValidationException(List<Throwable> errors) {
        super(buildMessage(errors));
        this.errors = errors;
    }

    public List<Throwable> errors() {
        return this.errors;
    }

    private static String buildMessage(List<Throwable> errors) {
        return io.vavr.collection.List.ofAll(errors).map(Throwable::getMessage).mkString(", ");
    }
}
