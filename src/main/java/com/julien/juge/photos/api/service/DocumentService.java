package com.julien.juge.photos.api.service;

import com.google.common.io.ByteStreams;
import com.julien.juge.photos.api.models.Document;
import com.julien.juge.photos.api.models.DocumentFactory;
import com.julien.juge.photos.api.utils.BinaryExtractor;
import com.julien.juge.photos.api.utils.DocumentPath;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.reactivecouchbase.json.JsArray;
import org.reactivecouchbase.json.JsValue;
import org.reactivecouchbase.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rx.Observable;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.print.Doc;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class DocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    private final CMISServiceImpl cmisService;
    private final BinaryExtractor binaryExtractor;

    @Autowired
    public DocumentService(CMISServiceImpl cmisService, BinaryExtractor binaryExtractor) {
        this.cmisService = cmisService;
        this.binaryExtractor = binaryExtractor;
    }

    public Session getSession() {
        return cmisService.getSession().defaultIfEmpty(null).toBlocking().first();
    }

    public Observable<Document> create(InputStreamResource file,
                                       String filename,
                                       String contentType,
                                       String path) {
        return create(file, new DocumentPath(path), filename, contentType);
    }

    public Observable<Document> create(InputStreamResource file,
                                       DocumentPath path,
                                       String filename,
                                       String contentType) {
        LOGGER.info("Creating file with filename {}, contentType {} at {}", filename, contentType, path);
        return cmisService
                .createDocument(path.toString(), filename, binaryExtractor.extract(file), contentType)
                .map(DocumentFactory::create);
    }

    public Observable<Document> create(InputStreamResource file,
                                       DocumentPath path,
                                       String filename,
                                       String contentType,
                                       Map<String, Object> metadata) {
        LOGGER.info("Creating file with filename {}, contentType {} at {} with meta {}",
                filename, contentType, path, metadata);
        return cmisService
                .createDocument(path.toString(), filename, binaryExtractor.extract(file), contentType, metadata)
                .map(DocumentFactory::create);
    }

    public Observable<Document> create(MultipartFile file, String path) {
        return create(file, new DocumentPath(path));
    }

    public Observable<Document> create(MultipartFile file, DocumentPath path) {
        LOGGER.info("Creating file at {} ", path);
        return create(file, path, file.getOriginalFilename());
    }

    public Observable<Document> create(MultipartFile file, DocumentPath path, String filename) {
        LOGGER.info("Creating file {} at {} ", filename, path);
        return cmisService
                .createDocument(path.toString(),
                        filename,
                        binaryExtractor.extract(file),
                        file.getContentType())
                .map(DocumentFactory::create);
    }

    public Observable<Document> create(MultipartFile file, DocumentPath path, Map<String, Object> metadata) {
        LOGGER.info("Creating file at {} ", path);
        return create(file, path, file.getOriginalFilename(), metadata);
    }

    public Observable<Document> create(MultipartFile file, DocumentPath path, String filename, Map<String, Object> metadata) {
        LOGGER.info("Creating file {} at {} with meta {} ", filename, path, metadata);
        return cmisService
                .createDocument(path.toString(),
                        filename,
                        binaryExtractor.extract(file),
                        file.getContentType(),
                        metadata)
                .map(DocumentFactory::create);
    }

    public Observable<InputStreamResource> downloadById(String id) {
        LOGGER.info("Downloading file with id {}", id);
        return cmisService.downloadById(id);
    }

    public Observable<InputStreamResource> downloadByPath(String path) {
        return downloadByPath(new DocumentPath(path));
    }

    public Observable<InputStreamResource> downloadByPath(DocumentPath path) {
       LOGGER.info("Downloading file at path {}", path);
        return cmisService.downloadByPath(path.toString());
    }

    public Observable<Folder> createFolder(DocumentPath documentPath) {
        return cmisService.createFolder(documentPath.toString());
    }

    public Observable<Boolean> copyDocument(Folder destinationFolder, String documentId) {
        return Observable.just(cmisService.copyById(destinationFolder, documentId)).map(document -> true);
    }

    public Observable<Boolean> delete(String id) {
        LOGGER.info("Deleting file with id {}", id);
        return cmisService.deleteById(id);
    }

    public Observable<Folder> findAllFolder(DocumentPath folderPath) {
        return cmisService.getChildrenFolder(folderPath.toString());
    }

    public Observable<Document> findAllByPath(DocumentPath folderPath) {
        LOGGER.info("Looking for all documents in {}", folderPath);
        return cmisService.getChildrenDocument(folderPath.toString())
                .map(doc -> DocumentFactory.create(doc, getBase64OfDoc(doc.getId())));
    }

    public Tuple2<String, String> getBase64OfDoc(String id) {
        return this.downloadById(id).map(inputStreamResource -> {
            String base64 = "";
            byte[] imageByte;
            String thumb = "";
            try {
                InputStream is = inputStreamResource.getInputStream();
                byte[] targetArray = ByteStreams.toByteArray(is);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

//                BufferedImage  img = ImageIO.read(is);
//                img.getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH);

                BASE64Encoder encoder = new BASE64Encoder();
//                thumb = encoder.encode(imageBytes);
                base64 = encoder.encode(targetArray);

                bos.close();


            } catch (IOException e) {
                log.error("Error during encoding file in Base64", e);
            }
            return Tuple.of(base64, thumb);
        }).toBlocking().first();
    }

    public Observable<Document> getByPath(String path) {
        return getByPath(new DocumentPath(path));
    }

    public Observable<Document> getByPath(DocumentPath path) {
        LOGGER.info("Getting file with at {}", path);
        return cmisService
                .getDocumentByPath(path.toString())
                .map(DocumentFactory::create);
    }

	public Observable<Boolean> isExists(DocumentPath path) {
		LOGGER.info("Looking up the existence of {}", path);
		return cmisService.isExists(path.toString());
	}
}
