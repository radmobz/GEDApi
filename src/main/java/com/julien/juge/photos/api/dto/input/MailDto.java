package com.julien.juge.photos.api.dto.input;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MailDto {

    @NotNull(message = "L'adresse email est obligatoire")
    protected String emailAddress;

    @NotNull(message = "Le contenu du message est obligatoire")
    protected String emailBody;

    @NotNull(message = "Le sujet du message est obligatoire")
    protected String emailSubject;

}
