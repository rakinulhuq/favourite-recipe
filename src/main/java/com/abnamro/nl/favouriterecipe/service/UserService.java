package com.abnamro.nl.favouriterecipe.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	void deleteUserIfExists(String username);
}
