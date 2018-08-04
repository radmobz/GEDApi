package com.julien.juge.photos.api.dto;

import com.julien.juge.photos.api.dto.output.EventDto;
import com.julien.juge.photos.api.dto.output.PostDto;
import com.julien.juge.photos.api.dto.output.TextDto;
import com.julien.juge.photos.api.entity.EventEntity;
import com.julien.juge.photos.api.entity.PostEntity;
import com.julien.juge.photos.api.entity.TextEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DtoBuilder {

    public static List<PostDto> buildListPostDtoOutput(List<PostEntity> posts) {
        return io.vavr.collection.List.ofAll(posts).map(DtoBuilder::buildPostDtoOutput).toJavaList();
    }

    public static PostDto buildPostDtoOutput(PostEntity postEntity) {
        return PostDto.builder()
                .createdDate(postEntity.getDate())
                .id(postEntity.getId())
                .event(DtoBuilder.buildEventDtoOutput(postEntity.getEvent()))
                .text(DtoBuilder.buildTextDtoOutput(postEntity.getText()))
                .build();
    }

    public static EventDto buildEventDtoOutput(EventEntity eventEntity) {
        return null != eventEntity ?
                EventDto.builder()
                        .comment(eventEntity.getComment())
                        .endDate(eventEntity.getEndDate())
                        .place(eventEntity.getPlace())
                        .price(eventEntity.getPrice())
                        .startDate(eventEntity.getStartDate())
                        .build()
                :
               null;
    }

    public static TextDto buildTextDtoOutput(TextEntity textEntity) {
        return null != textEntity ?
                TextDto.builder().text(textEntity.getText()).build()
                :
                null;
    }


}
