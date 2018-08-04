package com.julien.juge.photos.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.reactivecouchbase.json.JsObject;
import org.reactivecouchbase.json.Json;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;


@RestController
@RequestMapping("/v1/users")
@Slf4j
public class UserController {

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsObject>> getUsers() {
        return Observable.just(Json.obj().with("name", "son petit nom")).map(entries -> entries).map(ResponseEntity::ok).doOnError(e -> log.error("Error during getUsers", e));

    }

}
