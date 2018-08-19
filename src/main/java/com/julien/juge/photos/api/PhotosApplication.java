package com.julien.juge.photos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class PhotosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotosApplication.class, args);
	}

}
