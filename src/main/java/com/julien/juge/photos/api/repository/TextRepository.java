package com.julien.juge.photos.api.repository;

import com.julien.juge.photos.api.entity.TextEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends MongoRepository<TextEntity, String> {

}
