package com.abnamro.nl.favouriterecipe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.abnamro.nl.favouriterecipe.Constants;
import com.abnamro.nl.favouriterecipe.dto.request.AuthenticationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.RegistrationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.TokenRenewRequest;
import com.abnamro.nl.favouriterecipe.dto.response.AuthenticationResponse;
import com.abnamro.nl.favouriterecipe.dto.response.RegistrationResponse;
import com.abnamro.nl.favouriterecipe.dto.response.TokenRenewResponse;
import com.abnamro.nl.favouriterecipe.exception.ExceptionResponse;
import com.abnamro.nl.favouriterecipe.model.auth.User;
import com.abnamro.nl.favouriterecipe.service.AuthService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIT {

	private static final String AUTH_BASE_URI = "/auth";

	private HttpHeaders headers = new HttpHeaders();

	@LocalServerPort
    private int port;
	
	private AuthService authService;
	private TestRestTemplate restTemplate;
	
	@Autowired
	public AuthControllerIT(AuthService authService, TestRestTemplate restTemplate) {
		this.authService = authService;
		this.restTemplate = restTemplate;
	}
	
	@BeforeEach
	public void init() {
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void registerUser_successfulRequest() {
		RegistrationRequest registrationRequest = registrationRequest(Constants.USERNAME);
		HttpEntity<RegistrationRequest> entity = new HttpEntity<RegistrationRequest>(registrationRequest, headers);
		ResponseEntity<RegistrationResponse> response = restTemplate.exchange(
		          createURLWithPort("/signup"), HttpMethod.POST, entity, RegistrationResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        RegistrationResponse registrationResponse = response.getBody();
        assertNotNull(registrationResponse);
        authService.deleteUser(registrationResponse.getId());
	}
	
	@Test
	public void registerUser_failedRequest_usernameExists() {
		RegistrationRequest registrationRequest = registrationRequest(Constants.USERNAME);
		User user = authService.createUser(registrationRequest);
		HttpEntity<RegistrationRequest> entity = new HttpEntity<RegistrationRequest>(registrationRequest, headers);
		ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
		          createURLWithPort("/signup"), HttpMethod.POST, entity, ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(String.format("Username %s is already taken!", Constants.USERNAME), response.getBody().getErrorMessage());
        authService.deleteUser(user.getId());
	}
	
	@Test
	public void registerUser_failedRequest_emailExists() {
		RegistrationRequest registrationRequest = registrationRequest(Constants.USERNAME);
		User user = authService.createUser(registrationRequest);
		registrationRequest.setUsername(Constants.USERNAME + Constants.USERNAME);
		HttpEntity<RegistrationRequest> entity = new HttpEntity<RegistrationRequest>(registrationRequest, headers);
		ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
		          createURLWithPort("/signup"), HttpMethod.POST, entity, ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(String.format("Email %s is already in use!", Constants.EMAIL), response.getBody().getErrorMessage());
        authService.deleteUser(user.getId());
	}
	
	@Test
	public void registerUser_nullRequestBody() {
		HttpEntity<RegistrationRequest> entity = new HttpEntity<RegistrationRequest>(null, headers);
		ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
		          createURLWithPort("/signup"), HttpMethod.POST, entity, ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void authenticateUser_successfulRequest() {
		User user = authService.createUser(registrationRequest(Constants.USERNAME));
		AuthenticationRequest authenticationRequest = authenticationRequest();
		HttpEntity<AuthenticationRequest> entity = new HttpEntity<AuthenticationRequest>(authenticationRequest, headers);
		ResponseEntity<AuthenticationResponse> response = restTemplate.exchange(
		          createURLWithPort("/signin"), HttpMethod.POST, entity, AuthenticationResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthenticationResponse authenticationResponse = response.getBody();
        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getAccessToken());
        assertNotNull(authenticationResponse.getRefreshToken());

        authService.deleteUser(user.getId());
	}
	
	@Test
	public void authenticateUser_nullRequestBody() {
		HttpEntity<AuthenticationRequest> entity = new HttpEntity<AuthenticationRequest>(null, headers);
		ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
		          createURLWithPort("/signin"), HttpMethod.POST, entity, ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void renewToken_successfulRequest() {
		User user = authService.createUser(registrationRequest(Constants.USERNAME));
		AuthenticationResponse authenticationResponse = authService.authenticateUser(authenticationRequest());
		TokenRenewRequest tokenRenewRequest = tokenRenewRequest(authenticationResponse.getRefreshToken());
		HttpEntity<TokenRenewRequest> entity = new HttpEntity<TokenRenewRequest>(tokenRenewRequest, headers);
		ResponseEntity<TokenRenewResponse> response = restTemplate.exchange(
		          createURLWithPort("/renewtoken"), HttpMethod.POST, entity, TokenRenewResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TokenRenewResponse tokenRenewResponse = response.getBody();
        assertNotNull(tokenRenewResponse);
        assertNotNull(tokenRenewResponse.getAccessToken());
        assertNotNull(tokenRenewResponse.getRefreshToken());

        authService.deleteUser(user.getId());
	}
	
	@Test
	public void renewToken_nullRequestBody() {
		HttpEntity<TokenRenewRequest> entity = new HttpEntity<TokenRenewRequest>(null, headers);
		ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
		          createURLWithPort("/renewtoken"), HttpMethod.POST, entity, ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void renewToken_invalidRefreshToken() {
		TokenRenewRequest tokenRenewRequest = tokenRenewRequest(Constants.INVALID_REFRESH_TOKEN);
		HttpEntity<TokenRenewRequest> entity = new HttpEntity<TokenRenewRequest>(tokenRenewRequest, headers);
		ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
		          createURLWithPort("/renewtoken"), HttpMethod.POST, entity, ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(String.format("[%s] is an invalid Refresh Token!", Constants.INVALID_REFRESH_TOKEN), response.getBody().getErrorMessage());
	}
	
	private RegistrationRequest registrationRequest(String username) {
		return RegistrationRequest
				.builder()
				.username(username)
				.password(Constants.PASSWORD)
				.email(Constants.EMAIL)
				.role(null)
				.build();
	}
	
	private AuthenticationRequest authenticationRequest() {
		return AuthenticationRequest
				.builder()
				.username(Constants.USERNAME)
				.password(Constants.PASSWORD)
				.build();
	}
	
	private TokenRenewRequest tokenRenewRequest(String refreshToken) {
		return TokenRenewRequest
				.builder()
				.refreshToken(refreshToken)
				.build();
	}
	
	private String createURLWithPort(String uri) {
        return "http://localhost:" + port + AUTH_BASE_URI + uri;
    }
}
