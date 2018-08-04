package com.julien.juge.photos.api.utils.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message, Object... args) {
        super(String.format(message, args));
    }
}
