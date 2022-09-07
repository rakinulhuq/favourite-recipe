package com.abnamro.nl.favouriterecipe.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse("Access denied!", HttpStatus.FORBIDDEN), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }
	
	@ExceptionHandler({ UserRegistrationException.class })
    public ResponseEntity<ExceptionResponse> handleUserRegistrationException(UserRegistrationException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({ TokenRefreshException.class })
    public ResponseEntity<ExceptionResponse> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({ UsernameNotFoundException.class })
    public ResponseEntity<ExceptionResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler({ HttpMessageNotReadableException.class })
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({ Exception.class })
    public ResponseEntity<ExceptionResponse> handleException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
