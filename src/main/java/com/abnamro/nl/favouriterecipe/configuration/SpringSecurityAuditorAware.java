package com.abnamro.nl.favouriterecipe.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.abnamro.nl.favouriterecipe.model.UserDetailsImpl;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.ofNullable(SecurityContextHolder.getContext())
	            .map(SecurityContext::getAuthentication)
	            .filter(Authentication::isAuthenticated)
	            .map(Authentication::getPrincipal)
	            .map(x ->  x instanceof UserDetailsImpl userDetails ? userDetails.getId() : "");
	}
}
