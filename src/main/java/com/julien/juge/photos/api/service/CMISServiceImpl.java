package com.julien.juge.photos.api.service;

import com.julien.juge.photos.api.utils.CMISSession;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Service gérant les opérations CMIS
 */
@Service
public class CMISServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CMISServiceImpl.class);

    private final Session session;

    private CMISSession cmisSession;

    @Autowired
    public CMISServiceImpl(@Lazy Session session, CMISSession cmisSession) {
        this.session = cmisSession.initialize();
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#createDocument(java.lang.String, java.lang.String, byte[], java.lang.String)
     */
    public Observable<Document> createDocument(String folderPath, String fileName, byte[] content, String mimeType) {

        Map<String, Object> documentProperties = getDefaultDocumentProperties();

        return this.createDocument(folderPath, fileName, content, mimeType, documentProperties);
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#createDocument(java.lang.String, java.lang.String, byte[], java.lang.String, java.util.Map)
     */
    public Observable<Document> createDocument(String folderPath,
                                               String fileName,
                                               byte[] content,
                                               String mimeType,
                                               Map<String, Object> documentProperties) {

        // Le répertoire (path) sera créé s'il n'existe pas
        return createTreeFolder(folderPath)
                .flatMap(folder -> createDocument(folder, fileName, content, mimeType, documentProperties));
    }

    /**
     * Créer un {@link Document} dans le répertoire spécifié.
     *
     * @param repertoire         répertoire cible
     * @param fileName           nom du fichier
     * @param content            contenu du fichier
     * @param mimeType           type MIME du fichier
     * @param documentProperties metadonnées
     * @return le document créé
     */
    private Observable<Document> createDocument(Folder repertoire,
                                                String fileName,
                                                byte[] content,
                                                String mimeType,
                                                Map<String, Object> documentProperties) {

        InputStream is = new ByteArrayInputStream(content);
        ContentStream contentStream = new ContentStreamImpl(fileName, new BigInteger(content), mimeType, is);

        documentProperties.put(PropertyIds.NAME, fileName);

        return Observable.just(repertoire.createDocument(documentProperties, contentStream, null));
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#createTreeFolder(java.lang.String)
     */
    public synchronized Observable<Folder> createTreeFolder(String path) {

        LOGGER.info("Creating Tree Folder at path {}", path);

        Folder folder = session.getRootFolder();

        if (session.existsPath(path)) {
            return getFolderByPath(path);
        }

        Path p = Paths.get(path);
        for (int i = 0; i < p.getNameCount(); i++) {

            // Extract subfolder name
            String subFolder = p.getName(i).toString();

            // Try to create
            folder = this.createSubFolder(subFolder, folder);
        }

        return Observable.just(folder);
    }

    /**
     * Create a new {@link Folder} with a given Name in the given parent folder
     *
     * @param folderName : if null, use rootFolder instead
     * @return
     */
    private Folder createSubFolder(String folderName, Folder parent) {

        Folder newFolder = null;

        // On part du repertoire parent, à défaut de la racine
        if (parent == null) {
            parent = session.getRootFolder();
        }

        LOGGER.info("Creating folder {} in parent folder {}", folderName, parent.getName());

        // Contrôler l'existence du repertoire
        if (!session.existsPath(parent.getPath(), folderName)) {
            LOGGER.info("Folder {} doesn't exists yet", folderName);

            Map<String, Object> folderProperties = new HashMap<>();
            folderProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
            folderProperties.put(PropertyIds.NAME, folderName);

            newFolder = parent.createFolder(folderProperties);

            LOGGER.info("return new folder {}", newFolder.getPath());
        } else {
            LOGGER.info("Folder already exists {}", folderName);

            newFolder = (Folder) session.getObjectByPath(parent.getPath(), folderName);

            LOGGER.info("return existing folder {}", newFolder.getPath());
        }

        return newFolder;
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#deleteById(java.lang.String)
     */
    public Observable<Boolean> deleteById(String id) {

        Boolean result = Boolean.FALSE;

        CmisObject obj = session.getObject(id);

        if (obj != null) {
            obj.delete();
            result = Boolean.TRUE;
        }

        return Observable.just(result);
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#downloadById(java.lang.String)
     */
    public Observable<InputStreamResource> downloadById(String id) {

        return getDocumentById(id)
                .map(Document::getContentStream)
                .map(ContentStream::getStream)
                .map(InputStreamResource::new);
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#downloadByPath(java.lang.String)
     */
    public Observable<InputStreamResource> downloadByPath(String documentPath) {

        return getDocumentByPath(documentPath)
                .map(Document::getContentStream)
                .map(ContentStream::getStream)
                .map(InputStreamResource::new);
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#getDocumentById(java.lang.String)
     */
    public Observable<Document> getDocumentById(String id) {
        return Observable.just(session.getObject(id))
                .filter(cmisObject -> cmisObject instanceof Document)
                .map(document -> (Document) document)
                .map(document -> document.getObjectOfLatestVersion(false));
    }

    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#getDocumentByPath(java.lang.String)
     */
    public Observable<Document> getDocumentByPath(String documentPath) {
        return Observable.just(session.getObjectByPath(documentPath))
                .filter(cmisObject -> cmisObject instanceof Document)
                .map(document -> (Document) document)
                .map(document -> document.getObjectOfLatestVersion(false));
    }


    /* (non-Javadoc)
     * @see com.altima.api.document.service.ICMISService#getChildrenDocument(java.lang.String)
     */
    public Observable<Document> getChildrenDocument(String folderPath) {
        return getFolderByPath(folderPath)
                .flatMap(this::getChildrenDocument);
    }

    /**
     * Renvoie les {@link Document} présents à la racine d'un {@link Folder}
     *
     * @param folder répertoire
     * @return Documents
     */
    private Observable<Document> getChildrenDocument(Folder folder) {

        return Observable.from(io.vavr.collection.List.ofAll(folder.getChildren())
                .filter(content -> content instanceof Document)
                .map(doc -> (Document) doc)
        );
    }

    /**
     * Obtenir un {@link Folder} depuis son path.
     *
     * @param folderPath chemin du folder
     * @return Folder
     */
    private Observable<Folder> getFolderByPath(String folderPath) {

        return Observable.just(session.getObjectByPath(folderPath))
                .filter(cmisObject -> cmisObject instanceof Folder)
                .map(folder -> (Folder) folder);
    }

    /**
     * Applique les métadonnées par défaut : ObjectType
     *
     * @return les métadonnées
     */
    private Map<String, Object> getDefaultDocumentProperties() {
        Map<String, Object> documentProperties = new HashMap<>();
        documentProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        return documentProperties;
    }

    public Observable<Boolean> isExists(String documentPath) {
        return Observable.<Boolean>just(session.existsPath(documentPath));
    }

    public Observable<Session> getSession() {
        return Observable.just(this.session);
    }
}
