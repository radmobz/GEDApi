package com.julien.juge.photos.api.utils;

import com.julien.juge.photos.api.utils.exception.InvalidInputException;
import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;


@Slf4j
@Service
public class BindingResultUtils {

    private BindingResultUtils() {
    }

    public static void checkErrors(Errors errors) {
        if (errors != null && errors.hasErrors()) {
            List<String> errorMessages = List.of();
            for (FieldError fieldError : errors.getFieldErrors()) {
                errorMessages = errorMessages.append(fieldError.getDefaultMessage());
            }
            throw new InvalidInputException(errorMessages);
        }
    }
}
