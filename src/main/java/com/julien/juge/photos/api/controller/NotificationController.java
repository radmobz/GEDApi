package com.julien.juge.photos.api.controller;

import com.julien.juge.photos.api.dto.input.MailDto;
import com.julien.juge.photos.api.utils.BindingResultUtils;
import com.julien.juge.photos.api.service.MailService;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.reactivecouchbase.json.JsObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/notifications")
@Slf4j
public class NotificationController {

    private MailService mailService;

    @Autowired
    public NotificationController(MailService mailService) {
        this.mailService = mailService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<JsObject>> sendNotificationEmail(@RequestBody @Valid MailDto mailDto, Errors errors) throws MailjetSocketTimeoutException, MailjetException {

        BindingResultUtils.checkErrors(errors);

        return mailService.sendNotificationEmail(mailDto).map(ResponseEntity::ok).doOnError(e -> log.error("Problem during notification email send", e));

    }


}
