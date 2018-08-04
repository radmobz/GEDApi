package com.julien.juge.photos.api.dto.output;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Builder
@Data
public class EventDto {

    protected Date startDate;

    protected Date endDate;

    protected String place;

    protected String comment;

    protected Float price;

}
