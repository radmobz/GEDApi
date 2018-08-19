package com.julien.juge.photos.api.controller;

import com.google.common.io.ByteStreams;
import com.julien.juge.photos.api.dto.input.DocumentRequestDto;
import com.julien.juge.photos.api.dto.input.MailDto;
import com.julien.juge.photos.api.service.AccessService;
import com.julien.juge.photos.api.service.DocumentService;
import com.julien.juge.photos.api.service.MailService;
import com.julien.juge.photos.api.utils.DocumentPath;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import io.vavr.collection.List;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.reactivecouchbase.json.JsArray;
import org.reactivecouchbase.json.JsObject;
import org.reactivecouchbase.json.JsValue;
import org.reactivecouchbase.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import javax.validation.Valid;
import javax.wsdl.Output;
import java.io.ByteArrayOutputStream;
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

    private MailService mailService;

    @Autowired
    public DocumentController(DocumentService documentService, AccessService accessService, MailService mailService) {
        this.documentService = documentService;
        this.accessService = accessService;
        this.mailService = mailService;
    }

    @RequestMapping(
            path = "folders",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsArray>> getFolders(@RequestBody JsObject documentRequestDto) {

        DocumentPath documentPath = new DocumentPath("CLIENTS", documentRequestDto.string("clientId"), "SHOOTINGS");

        return accessService.isOk( documentRequestDto.string("clientId"), documentRequestDto.string("password"))
                .flatMap(isOk -> isOk ? documentService.findAllFolder(documentPath).map(folder -> folder.getName()).toList().map(Json::arr) : Observable.just(Json.obj().with("erreur", "mot de passe incorrect")).toList().map(Json::arr)).map(ResponseEntity::ok);

    }

    @RequestMapping(
            path = "test",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsValue>> geTest(@RequestBody JsValue request) {



        return Observable.just(Json.obj().with("test", "test")).map(ResponseEntity::ok);

    }

    @RequestMapping(
            path = "search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsArray>> getDocuments(@RequestBody JsObject documentRequestDto) {

        DocumentPath documentPath = new DocumentPath("CLIENTS", documentRequestDto.string("clientId"), "SHOOTINGS", documentRequestDto.string("albumId"));

        return accessService.isOk( documentRequestDto.string("clientId"), documentRequestDto.string("password"))
        .flatMap(isOk -> isOk ? documentService.findAllByPath(documentPath).map(document -> document.toJson()).toList().map(Json::arr) : Observable.just(Json.obj().with("erreur", "mot de passe incorrect")).toList().map(Json::arr)).map(ResponseEntity::ok);

    }

    @RequestMapping(
            path = "ask",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsValue>> askForPrepare(@RequestBody JsObject request) throws MailjetSocketTimeoutException, MailjetException {
        String clientId = request.string("clientId");
        String password = request.string("password");
        String albumId = request.string("albumId");

        DocumentPath documentPath = new DocumentPath("WORKS", clientId, albumId);
        JsArray array = request.array("photos");
        String url = "http://176.31.127.38:8080/share/page/repository?path=" + documentPath.toString().replace("/", "%2F");
        String subject = "Nouvelle demande client Num: " + clientId + " Pour album : " + albumId;
        log.info(url);

        return documentService.createFolder(documentPath)
                .map(folder -> {
                    array.forEach(jsValue -> documentService.copyDocument(folder, jsValue.string("id")));
                    try {
                        mailService.sendNotificationEmail(MailDto.builder().emailAddress("jjuge.julien@gmail.com").emailBody(url).emailSubject(subject).build());
                    } catch (MailjetSocketTimeoutException e) {
                        e.printStackTrace();
                    } catch (MailjetException e) {
                        e.printStackTrace();
                    }
                    return Json.obj();
                })
                .map(r -> Json.obj()).map(ResponseEntity::ok);
    }

    @RequestMapping(
            path = "vitrine",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsArray>> getVitrine(@RequestParam(name = "type") String type) {

        DocumentPath documentPath = new DocumentPath("PORTFOLIO", type);

        return documentService.findAllByPath(documentPath).map(document -> document.toJson()).toList().map(Json::arr).map(ResponseEntity::ok);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_PNG_VALUE)
    public Observable<ResponseEntity<byte[]>> getImage(@PathVariable String id) throws IOException {
        return documentService.downloadById(id).map(inputStreamResource -> {
            try {
                return IOUtils.toByteArray(inputStreamResource.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new byte[0];
        }).map(ResponseEntity::ok);
    }

    @RequestMapping(value = "/thumb/{id}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_PNG_VALUE)
    public Observable<ResponseEntity<byte[]>> getThumbImage(@PathVariable String id) throws IOException {
        return documentService.downloadById(id).map(inputStreamResource -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                Thumbnails.of(inputStreamResource.getInputStream())
                        .size(240, 159).toOutputStream(bos);
                return bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new byte[0];
        }).map(ResponseEntity::ok);
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
