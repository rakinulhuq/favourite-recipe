package com.abnamro.nl.favouriterecipe.controller;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abnamro.nl.favouriterecipe.dto.request.AuthenticationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.RegistrationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.TokenRenewRequest;
import com.abnamro.nl.favouriterecipe.dto.response.AuthenticationResponse;
import com.abnamro.nl.favouriterecipe.dto.response.RegistrationResponse;
import com.abnamro.nl.favouriterecipe.dto.response.TokenRenewResponse;
import com.abnamro.nl.favouriterecipe.model.auth.User;
import com.abnamro.nl.favouriterecipe.service.AuthService;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {
	
	private AuthService authService;
	
	@Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/signin")
	public ResponseEntity<AuthenticationResponse> authenticateUser(@Valid @RequestBody AuthenticationRequest authRequest) {
		AuthenticationResponse authenticationResponse = authService.authenticateUser(authRequest);
		return ResponseEntity.ok(authenticationResponse);
	}
	
	@PostMapping("/renewtoken")
	public ResponseEntity<TokenRenewResponse> renewToken(@Valid @RequestBody TokenRenewRequest tokenRenewRequest) {
		String token = authService.renewToken(tokenRenewRequest.getRefreshToken());
		return ResponseEntity.ok(
				new TokenRenewResponse(
						token, 
						tokenRenewRequest.getRefreshToken())
				);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
		User user = authService.createUser(registrationRequest);
		Set<String> assignedRoles = user.getRoles().stream().map(x -> x.getName().toString()).collect(Collectors.toSet());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				new RegistrationResponse(
						user.getId(),
						registrationRequest.getUsername(), 
						registrationRequest.getEmail(), 
						assignedRoles)
				);
	}
}
