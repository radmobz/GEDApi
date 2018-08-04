package com.julien.juge.photos.api;

import com.julien.juge.photos.api.config.mvc.WebMvcConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableAutoConfiguration
public class PhotosApplication {

	@Bean
	public WebMvcConfigurerAdapter rxJavaWebMvcConfiguration() {
		return new WebMvcConfiguration();
	}

	public static void main(String[] args) {
		SpringApplication.run(PhotosApplication.class, args);
	}
}
