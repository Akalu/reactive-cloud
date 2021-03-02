package com.cloud.service;

import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import com.cloud.model.Image;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageService {

	Flux<Image> findAllImages();

	Mono<Resource> findOneImage(String filename);

	Mono<Void> createImage(Flux<FilePart> files, Principal auth);

	Mono<Void> deleteImage(String filename);
}