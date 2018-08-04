package com.julien.juge.photos.api.service;

import com.julien.juge.photos.api.entity.AccessEntity;
import com.julien.juge.photos.api.repository.AccessRepository;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service
public class AccessService {

    private AccessRepository accessRepository;

    @Autowired
    public AccessService(AccessRepository accessRepository) {
        this.accessRepository = accessRepository;
    }

    public Observable<Boolean> isOk(String clientId, String password) {

        boolean retour = false;

        AccessEntity accessFinded = accessRepository.findByClientId(clientId);

        retour = null != accessFinded && Option.of(accessFinded.getPassword()).get().equals(password);

        return Observable.just(retour);
    }

}
