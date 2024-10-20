package com.paymybuddy.core.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class PayMyBuddyApiException extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiError> handlerEntityNotFoundException(EntityNotFoundException ex) {
		log.info("");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, ex.getMessage(), Instant.now()));
	}

}
