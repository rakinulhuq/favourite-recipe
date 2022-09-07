package com.abnamro.nl.favouriterecipe.configuration;

import org.springframework.security.core.Authentication;

import com.abnamro.nl.favouriterecipe.model.UserDetailsImpl;

public interface AuthenticationFacade {
	Authentication getAuthentication();
	UserDetailsImpl getLoggedInUser();
	String getLoggedInUserId();
}
