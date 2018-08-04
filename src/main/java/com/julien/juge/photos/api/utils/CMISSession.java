package com.julien.juge.photos.api.utils;

import okhttp3.HttpUrl;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CMISSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(CMISSession.class);

    private static final String ALFRESCO_HOST = "176.31.127.38";
    private static final String ALFRESCO_PORT = "8080";
    private static final String ALFRESCO_USERNAME = "admin";
    private static final String ALFRESCO_PASSWORD = "Vasedote11";

    public CMISSession() {
        this.initialize();
    }


    public Session initialize() {
        LOGGER.info("Initializing CMIS session");

        // default factory implementation of client runtime
        SessionFactory f = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<>();

        // user credentials
        parameter.put(SessionParameter.USER, ALFRESCO_USERNAME);
        parameter.put(SessionParameter.PASSWORD, ALFRESCO_PASSWORD);

        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, this.getAlfrescoUrl().build().toString());
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // create session wired on the first repository
        List<Repository> repositories = f.getRepositories(parameter);
        Repository repository = repositories.get(0);
        return repository.createSession();
    }

    /**
     * @return full url to alfresco CMIS Api
     */
    private HttpUrl.Builder getAlfrescoUrl() {

        return new HttpUrl.Builder()
                .scheme("http")
                .host(ALFRESCO_HOST)
                .port(Integer.parseInt(ALFRESCO_PORT))
                .addPathSegments("alfresco/api/-default-/public/cmis/versions/1.1/atom");
    }
}
