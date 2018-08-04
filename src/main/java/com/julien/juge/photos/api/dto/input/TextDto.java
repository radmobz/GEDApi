package com.julien.juge.photos.api.dto.input;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TextDto {

    @NotNull(message = "Le texte du post est obligatoire")
    protected String text;

}
