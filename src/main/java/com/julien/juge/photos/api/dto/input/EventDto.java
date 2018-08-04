package com.julien.juge.photos.api.dto.input;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class EventDto {

    @NotNull(message = "La date de debut est obligatoire")
    protected Date startDate;

    protected Date endDate;

    @NotNull(message = "L'emplacement est obligatoire")
    protected String place;

    protected String comment;

    protected Float price;

}
