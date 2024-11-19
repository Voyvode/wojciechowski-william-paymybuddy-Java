package com.paymybuddy.core.config;

import com.paymybuddy.core.error.ErrorInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.springframework.http.CacheControl.maxAge;

/**
 * Configuration class for setting up resource handlers and caching.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * Adds resource handlers for serving static resources.
	 *
	 * @param registry the registry for configuring resource handlers
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/css/**", "/resources/js/**", "/resources/img/**")
				.addResourceLocations("/resources/css/", "/resources/js/", "/resources/img/")
				.setCacheControl(maxAge(365, DAYS));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ErrorInterceptor());
	}

}


