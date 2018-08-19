package com.julien.juge.photos.api.models;

import lombok.Builder;
import lombok.Data;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.reactivecouchbase.functional.Option;
import org.reactivecouchbase.json.JsValue;
import org.reactivecouchbase.json.Json;

import java.time.LocalDateTime;

@Data
@Builder
public class Document {

    public final String name;
    public final String title;
    public final String type;
    public final String version;
    public final Long size;
    public final String id;
    public final String url;
    public final ContentStream content;
    public final LocalDateTime dateCreation;
    public final LocalDateTime dateModification;
    public final String path;
    public final String base64;
    public final String thumb;

    public JsValue toJson() {
        return Json.obj()
                .withString("name", Option.apply(this.name))
                .withString("title", Option.apply(this.title))
                .withString("type", Option.apply(this.type))
                .withString("version", Option.apply(this.version))
                .withLong("size", Option.apply(this.size))
                .withString("id", Option.apply(this.id))
                .withString("url", Option.apply(this.url))
                .withString("dateCreation", Option.apply(this.dateCreation).map(LocalDateTime::toString))
                .withString("dateModification", Option.apply(this.dateModification).map(LocalDateTime::toString))
                .with("content", Json.obj()
                        .withString("filename", Option.apply(content).map(ContentStream::getFileName)))
                .withString("mime-type", Option.apply(content).map(ContentStream::getMimeType))
                .withLong("length", Option.apply(content).map(ContentStream::getLength))
                .withString("path", Option.apply(path))
                .withString("base64", Option.apply(base64))
                .withString("thumb", Option.apply(thumb));
    }
}
