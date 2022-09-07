package com.abnamro.nl.favouriterecipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserRegistrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserRegistrationException(String message) {
        super(message);
    }
}
