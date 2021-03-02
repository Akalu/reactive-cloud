package com.cloud.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.cloud.data.ImageRepository;
import com.cloud.model.Image;

import static com.cloud.config.Constants.*;

/**
 * @author akaliutau
 */
@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

	private final ResourceLoader resourceLoader;
	private final ImageRepository imageRepository;

	private final MeterRegistry meterRegistry;

	public ImageServiceImpl(ResourceLoader resourceLoader, ImageRepository imageRepository,
			MeterRegistry meterRegistry) {

		this.resourceLoader = resourceLoader;
		this.imageRepository = imageRepository;
		this.meterRegistry = meterRegistry;
	}

	@Override
	public Flux<Image> findAllImages() {
		return imageRepository.findAll().log("findAll");
	}

	@Override
	public Mono<Resource> findOneImage(String filename) {
		return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename))
				.log("findOneImage");
	}

	@Override
	public Mono<Void> createImage(Flux<FilePart> files, Principal auth) {
		return files.log("createImage-files").flatMap(file -> {
			// create a record in the db first
			Mono<Image> saveDatabaseImage = imageRepository
					.save(new Image(UUID.randomUUID().toString(), file.filename(), auth.getName()))
					.log("createImage-save");
			// then upload file
			Mono<Void> copyFile = Mono.just(Paths.get(UPLOAD_ROOT, file.filename()).toFile())
					.log("createImage-picktarget").map(destFile -> {
						try {
							destFile.createNewFile();
							return destFile;
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					})
					.log("createImage-newfile")
					.flatMap(file::transferTo)
					.log("createImage-copy")
					.then(Mono.fromRunnable(() -> meterRegistry.summary("files.uploaded.bytes")
							.record(Paths.get(UPLOAD_ROOT, file.filename()).toFile().length())));

			return Mono.when(saveDatabaseImage, copyFile).log("createImage-when");
		}).log("createImage-flatMap").then().log("createImage-done");
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or " + "@imageRepository.findByName(#filename).owner " + "== authentication.name")
	public Mono<Void> deleteImage(String filename) {
		Mono<Void> deleteDatabaseImage = imageRepository.findByName(filename).log("deleteImage-find")
				.flatMap(imageRepository::delete).log("deleteImage-record");

		Mono<Object> deleteFile = Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).log("deleteImage-file");

		return Mono.when(deleteDatabaseImage, deleteFile).log("deleteImage-when").then().log("deleteImage-done");
	}

}
