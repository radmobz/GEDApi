package com.julien.juge.photos.api.controller;

import com.google.common.io.ByteStreams;
import com.ibm.wsdl.util.StringUtils;
import com.julien.juge.photos.api.service.DocumentServiceImpl;
import com.julien.juge.photos.api.utils.DocumentPath;
import lombok.extern.slf4j.Slf4j;
import org.reactivecouchbase.json.JsArray;
import org.reactivecouchbase.json.JsObject;
import org.reactivecouchbase.json.JsValue;
import org.reactivecouchbase.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


@RestController
@RequestMapping("/v1/documents")
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentServiceImpl documentService;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsArray>> getDocuments() {



        log.info(Json.obj().with("key", "value").stringify());

        DocumentPath documentPath = new DocumentPath("CLIENTS", "0000000001");

        return documentService.findAllByPath(documentPath).map(document -> document.toJson()).toList().map(Json::arr).map(ResponseEntity::ok);

    }

    @RequestMapping(
            path = "download",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsValue>> getDocumentById(@RequestParam(name = "documentId") String documentId) {
        return documentService.downloadById(documentId).map(inputStreamResource -> {
            String base64 = "";
            try {
                InputStream is = inputStreamResource.getInputStream();
                byte[] targetArray = ByteStreams.toByteArray(is);
                base64 = Base64.getEncoder().encodeToString(targetArray);
            } catch (IOException e) {
                log.error("Error during encoding file in Base64", e);
            }
            return base64;
        })
                .map(s -> Json.toJson(Json.obj().with("base64", s).with("docId", documentId)))
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsObject>> getUsers() {
        return Observable.just(Json.obj().with("name", "son petit nom")).map(entries -> entries).map(ResponseEntity::ok).doOnError(e -> log.error("Error during getUsers", e));

    }

}
