package com.cloud.controller;

import java.io.IOException;
import java.security.Principal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.model.Image;
import com.cloud.service.ImageService;

/**
 * @author akaliutau
 */
@Controller
public class ImageController {

	private static final String BASE_PATH = "/images";
	private static final String FILENAME = "{filename:.+}";

	private final ImageService imageService;

	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}

	@GetMapping(value = BASE_PATH)
	@ResponseBody
	public Flux<Image> allImages() {
		return imageService.findAllImages();
	}

	@GetMapping(value = BASE_PATH + "/" + FILENAME, produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename) {
		return imageService.findOneImage(filename).map(resource -> {
			try {
				return ResponseEntity.ok().contentLength(resource.contentLength())
						.body(new InputStreamResource(resource.getInputStream()));
			} catch (IOException e) {
				return ResponseEntity.badRequest().body("Couldn't find " + filename + " => " + e.getMessage());
			}
		});
	}

	@PostMapping(value = BASE_PATH)
	public Mono<ResponseEntity<?>> createFile(@RequestPart("file") Flux<FilePart> files,
			@AuthenticationPrincipal Principal principal) {
		return imageService.createImage(files, principal).thenReturn(ResponseEntity.accepted().build());
	}

	@DeleteMapping(BASE_PATH + "/" + FILENAME)
	public Mono<ResponseEntity<?>> deleteFile(@PathVariable String filename) {
		return imageService.deleteImage(filename).thenReturn(ResponseEntity.accepted().build());
	}
}
