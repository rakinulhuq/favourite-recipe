package com.abnamro.nl.favouriterecipe.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.abnamro.nl.favouriterecipe.model.UserDetailsImpl;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {

	@Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

	@Override
	public UserDetailsImpl getLoggedInUser() {
        return (UserDetailsImpl) getAuthentication().getPrincipal();
	}

	@Override
	public String getLoggedInUserId() {
		return getLoggedInUser().getId();
	}
	
}
