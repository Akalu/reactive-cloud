package com.cloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.converter.CompositeMessageConverterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author akaliutau
 */
@Configuration
public class AppConfig {

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * Needed to avoid 'requires a bean of type
	 * org.springframework.cloud.stream.converter.CompositeMessageConverterFactory'
	 * error
	 * 
	 * @return default factory
	 */
	@Bean
	public CompositeMessageConverterFactory transferMessageConverter() {
		return new CompositeMessageConverterFactory();
	}

}
