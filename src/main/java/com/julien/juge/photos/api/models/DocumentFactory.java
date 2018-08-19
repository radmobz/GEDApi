package com.julien.juge.photos.api.models;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DocumentFactory {

    private DocumentFactory(){

    }

    public static Document create(org.apache.chemistry.opencmis.client.api.Document doc) {

        return Document.builder()

                .id(doc.getId())
                .name(doc.getName())
                .url(doc.getContentUrl())
                .content(doc.getContentStream())
                .path(doc.getPaths().toString())
                .size(doc.getContentStreamLength())
                .version(doc.getVersionLabel())
                .dateCreation(LocalDateTime.ofInstant(
                        doc.getCreationDate().toInstant(),
                        ZoneId.systemDefault())
                )
                .dateModification(LocalDateTime.ofInstant(
                        doc.getLastModificationDate().toInstant(),
                        ZoneId.systemDefault())
                )
                .build();
    }

    public static Document create(org.apache.chemistry.opencmis.client.api.Document doc, Tuple2<String, String> bases64) {

        return Document.builder()

                .id(doc.getId())

                .name(doc.getName())
                .url(doc.getContentUrl())
                .content(doc.getContentStream())
                .path(doc.getPaths().get(0).toString())
                .size(doc.getContentStreamLength())
                .version(doc.getVersionLabel())
                .dateCreation(LocalDateTime.ofInstant(
                        doc.getCreationDate().toInstant(),
                        ZoneId.systemDefault())
                )
                .dateModification(LocalDateTime.ofInstant(
                        doc.getLastModificationDate().toInstant(),
                        ZoneId.systemDefault())
                )
                .base64(bases64._1)
//                .thumb(bases64._2)
                .build();
    }
}
