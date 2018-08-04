package com.julien.juge.photos.api.dto.input;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DocumentRequestDto {

    @NotNull(message = "Identifiant du client obligatoire")
    protected String clientId;

    @NotNull(message = "Le mot de passe est obligatoire")
    protected String password;

}

