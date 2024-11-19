package com.paymybuddy.core.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class ErrorInterceptor implements HandlerInterceptor {
	@Override
	public void afterCompletion(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull Object handler, Exception ex) {
		if (response.getStatus() >= 400) {
			String message = switch (response.getStatus()) {
				case 404 -> "Page Not Found";
				case 500 -> "Internal Server Error";
				default -> throw new IllegalStateException("Unexpected value: " + response.getStatus());
			};

			log.warn("HTTP Error {}: {}", response.getStatus(), message);
		}
	}
}