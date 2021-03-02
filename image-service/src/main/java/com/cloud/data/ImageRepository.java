package com.cloud.data;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.cloud.model.Image;

/**
 * @author akaliutau
 */
@Repository
public interface ImageRepository extends ReactiveMongoRepository<Image, String> {

	Mono<Image> findByName(String name);
}
