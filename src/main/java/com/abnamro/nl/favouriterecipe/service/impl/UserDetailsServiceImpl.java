package com.abnamro.nl.favouriterecipe.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abnamro.nl.favouriterecipe.model.UserDetailsImpl;
import com.abnamro.nl.favouriterecipe.model.auth.User;
import com.abnamro.nl.favouriterecipe.repository.UserRepository;
import com.abnamro.nl.favouriterecipe.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserService {
	
	private UserRepository userRepository;
	
	@Autowired
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		return UserDetailsImpl.build(user);
	}

	@Override
	public void deleteUserIfExists(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			userRepository.delete(user.get());
		}		
	}
}
