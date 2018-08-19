package com.julien.juge.photos.api.service;

import com.julien.juge.photos.api.dto.input.MailDto;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivecouchbase.json.JsObject;
import org.reactivecouchbase.json.Json;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service
@Slf4j
public class MailService {

    @Value("${mailjet.apikey}")
    private String apikey;

    @Value("${mailjet.apisecret}")
    private String apisecret;


    public Observable<JsObject> sendNotificationEmail(MailDto mailDto) throws MailjetSocketTimeoutException, MailjetException {

        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        client = new MailjetClient(apikey, apisecret, new ClientOptions("v3.1"));
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "julien.juge@live.fr")
                                        .put("Name", "DELPHINE PHOTOGRAPHIE"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", mailDto.getEmailAddress())
                                                .put("Name", mailDto.getEmailAddress())))
                                .put(Emailv31.Message.SUBJECT, mailDto.getEmailSubject())
                                .put(Emailv31.Message.TEXTPART, mailDto.getEmailBody())
                                .put(Emailv31.Message.HTMLPART, "<p>" + mailDto.getEmailBody() + "</p>")));
        response = client.post(request);

        return response.getStatus() == 200 ? Observable.just(Json.obj().with("response", "ok")) : Observable.just(Json.obj().with("response", "ko"));

    }

}
