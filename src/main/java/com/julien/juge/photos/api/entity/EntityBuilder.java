package com.julien.juge.photos.api.entity;

import com.julien.juge.photos.api.dto.input.PostDto;
import com.julien.juge.photos.api.dto.input.EventDto;
import com.julien.juge.photos.api.dto.input.TextDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityBuilder {

    public static List<PostEntity> buildListPostEntity(List<PostDto> posts) {
        return io.vavr.collection.List.ofAll(posts).map(EntityBuilder::buildPostEntity).toJavaList();
    }

    public static PostEntity buildPostEntity(PostDto post) {
        return PostEntity.builder()
                .date(post.getCreatedDate())
                .event(EntityBuilder.buildEventEntity(post.getEvent()))
                .text(EntityBuilder.buildTextEntity(post.getText()))
                .build();
    }

    public static EventEntity buildEventEntity(EventDto event) {
        return null != event ? EventEntity.builder().comment(event.getComment()).endDate(event.getEndDate()).startDate(event.getStartDate()).place(event.getPlace()).price(event.getPrice()).build() : null;
    }

    public static TextEntity buildTextEntity(TextDto text) {
        return null != text ? TextEntity.builder()
                .text(text.getText())
                .build() : null;
    }

}
