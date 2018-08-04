package com.julien.juge.photos.api.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "Post")
public class PostEntity {

    @Id
    private String id;

    private Date date;

    private EventEntity event;

    private TextEntity text;
}
