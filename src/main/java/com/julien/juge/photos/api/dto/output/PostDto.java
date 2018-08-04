package com.julien.juge.photos.api.dto.output;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PostDto {

    private String id;

    private Date createdDate;

    private EventDto event;

    private TextDto text;

}
