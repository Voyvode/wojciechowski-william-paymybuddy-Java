package com.paymybuddy.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.springframework.http.CacheControl.maxAge;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/css/**", "/resources/js/**", "/resources/img/**")
				.addResourceLocations("/resources/css/", "/resources/js/", "/resources/img/")
				.setCacheControl(maxAge(365, DAYS));
	}

}