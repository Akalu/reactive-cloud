package com.cloud.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableMongoRepositories(basePackages = { "com.cloud.data" })
@Slf4j
public class MongoJPAConfig {

	@Value("${mongo.database}")
	private String database;

	@Value("${mongo.host}")
	private String host;

	@Value("${mongo.port}")
	private String port;

	@Value("${mongo.username}")
	private String username;

	@Value("${mongo.password}")
	private String password;

	@Value("${mongo.timeout}")
	private int timeout;

	@Bean
	public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
		return new ReactiveMongoTemplate(mongoClient, database);
	}

	@Bean
	public MongoClient mongoClient() {

		MongoCredential creds = MongoCredential.createCredential(username, "admin", password.toCharArray());
		ConnectionString connectionString = new ConnectionString("mongodb://" + host + ":" + port + "/" + database);
		MongoClientSettings settings = MongoClientSettings.builder().credential(creds)
				.applyConnectionString(connectionString).build();
		MongoClient client = MongoClients.create(settings);
		log.info("======== MongoDb init complete ========");
		
		return client;
	}
}
