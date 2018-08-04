package com.julien.juge.photos.api.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "Access")
public class AccessEntity {

    @Id
    private String id;

    private String clientId;

    private String password;

}
