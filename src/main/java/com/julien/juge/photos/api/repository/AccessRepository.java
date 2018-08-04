package com.julien.juge.photos.api.repository;

import com.julien.juge.photos.api.entity.AccessEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository extends MongoRepository<AccessEntity, String> {

    AccessEntity findByClientId(String clientId);

}
