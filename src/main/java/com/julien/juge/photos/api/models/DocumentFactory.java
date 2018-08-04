package com.julien.juge.photos.api.models;

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

    public static Document create(org.apache.chemistry.opencmis.client.api.Document doc, String base64) {

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
                .base64(base64)
                .build();
    }
}
