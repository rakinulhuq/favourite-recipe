package com.abnamro.nl.favouriterecipe.service;

import com.abnamro.nl.favouriterecipe.dto.request.AuthenticationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.RegistrationRequest;
import com.abnamro.nl.favouriterecipe.dto.response.AuthenticationResponse;
import com.abnamro.nl.favouriterecipe.model.auth.User;

public interface AuthService {
	User createUser(RegistrationRequest registrationRequest);
	AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest);
	String renewToken(String refreshToken);
	void deleteUser(String userId);
}
