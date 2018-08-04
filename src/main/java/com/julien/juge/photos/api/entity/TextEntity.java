package com.julien.juge.photos.api.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "Text")
public class TextEntity {

    private String text;

}
