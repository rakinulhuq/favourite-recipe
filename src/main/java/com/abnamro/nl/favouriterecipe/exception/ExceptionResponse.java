package com.abnamro.nl.favouriterecipe.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExceptionResponse {
	private String errorMessage;
	private HttpStatus status;
}
