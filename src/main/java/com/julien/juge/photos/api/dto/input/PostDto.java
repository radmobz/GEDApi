package com.julien.juge.photos.api.dto.input;

import lombok.Data;

import java.util.Date;

@Data
public class PostDto {

    protected String id;

    protected Date createdDate;

    protected EventDto event;

    protected TextDto text;

}
