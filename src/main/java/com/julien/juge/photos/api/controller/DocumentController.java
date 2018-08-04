package com.julien.juge.photos.api.controller;

import com.google.common.io.ByteStreams;
import com.julien.juge.photos.api.dto.input.DocumentRequestDto;
import com.julien.juge.photos.api.service.AccessService;
import com.julien.juge.photos.api.service.DocumentService;
import com.julien.juge.photos.api.utils.DocumentPath;
import lombok.extern.slf4j.Slf4j;
import org.reactivecouchbase.json.JsArray;
import org.reactivecouchbase.json.JsObject;
import org.reactivecouchbase.json.JsValue;
import org.reactivecouchbase.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


@RestController
@RequestMapping("/v1/documents")
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController {

    private DocumentService documentService;

    private AccessService accessService;

    @Autowired
    public DocumentController(DocumentService documentService, AccessService accessService) {
        this.documentService = documentService;
        this.accessService = accessService;
    }


    @RequestMapping(
            path = "search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsArray>> getDocuments(@RequestBody @Valid DocumentRequestDto documentRequestDto, Errors errors) {

        DocumentPath documentPath = new DocumentPath("CLIENTS", documentRequestDto.getClientId());

        return accessService.isOk(documentRequestDto.getClientId(), documentRequestDto.getPassword())
        .flatMap(isOk -> isOk ? documentService.findAllByPath(documentPath).map(document -> document.toJson()).toList().map(Json::arr) : Observable.just(Json.obj().with("erreur", "mot de passe incorrect")).toList().map(Json::arr)).map(ResponseEntity::ok);

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
