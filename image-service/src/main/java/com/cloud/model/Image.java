package com.cloud.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author akaliutau
 */
@Data
@AllArgsConstructor
@ToString
public class Image {

	@Id
	private String id;

	private String name;
	private String owner;
}
