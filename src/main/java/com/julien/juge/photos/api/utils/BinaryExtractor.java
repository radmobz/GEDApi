package com.julien.juge.photos.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class BinaryExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryExtractor.class);

    /**
     * Extraire le contenu d'un {@link MultipartFile} au format <code>byte[]</code>
     *
     * @param file fichier à exraire
     * @return contenu du fichier au format <code>byte[]</code>
     */
    public byte[] extract(MultipartFile file) {

        try {
            return file.getBytes();
        } catch (IOException e) {
            LOGGER.error("Error occured during extraction of the binary content for document", e);
            return new byte[0];
        }
    }

    /**
     * Extraire le contenu d'un {@link MultipartFile} au format <code>byte[]</code>
     *
     * @param file fichier à exraire
     * @return contenu du fichier au format <code>byte[]</code>
     */
    public byte[] extract(InputStreamResource file) {

        try {
            return StreamUtils.copyToByteArray(file.getInputStream());
        } catch (IOException e) {
            LOGGER.error("Error occured during extraction of the binary content for document", e);
            return new byte[0];
        }
    }
}
