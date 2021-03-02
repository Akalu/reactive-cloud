package com.cloud.data;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import lombok.extern.slf4j.Slf4j;

import static com.cloud.config.Constants.*;

/**
 * Initial data - for testing only
 * @author akaliutau
 */
@Component
@Slf4j
public class InitDatabase {

	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations) {
		return args -> {
			
			// copy all resources in /images to ./uploaded
			File files = Paths.get(getClass().getResource("/images").toURI()).toFile();
			for (File file : files.listFiles()) {
				log.info("copy {}", file);
				FileCopyUtils.copy(file, Paths.get(UPLOAD_ROOT, file.getName()).toFile());
			}

			log.info("======== all files copied ========");
			
		};
	}
	


}