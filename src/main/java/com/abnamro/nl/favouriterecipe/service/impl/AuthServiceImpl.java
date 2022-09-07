package com.abnamro.nl.favouriterecipe.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abnamro.nl.favouriterecipe.configuration.JwtUtils;
import com.abnamro.nl.favouriterecipe.dto.request.AuthenticationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.RegistrationRequest;
import com.abnamro.nl.favouriterecipe.dto.response.AuthenticationResponse;
import com.abnamro.nl.favouriterecipe.exception.TokenRefreshException;
import com.abnamro.nl.favouriterecipe.exception.UserRegistrationException;
import com.abnamro.nl.favouriterecipe.model.UserDetailsImpl;
import com.abnamro.nl.favouriterecipe.model.auth.RefreshToken;
import com.abnamro.nl.favouriterecipe.model.auth.Role;
import com.abnamro.nl.favouriterecipe.model.auth.User;
import com.abnamro.nl.favouriterecipe.model.auth.UserRole;
import com.abnamro.nl.favouriterecipe.repository.RoleRepository;
import com.abnamro.nl.favouriterecipe.repository.UserRepository;
import com.abnamro.nl.favouriterecipe.service.AuthService;
import com.abnamro.nl.favouriterecipe.service.RefreshTokenService;

@Service
public class AuthServiceImpl implements AuthService {

	private static final String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found.";
	
	private AuthenticationManager authenticationManager;
	
	private PasswordEncoder encoder;
	
	private UserRepository userRepository;
	
	private RoleRepository roleRepository;
	
	private RefreshTokenService refreshTokenService;
	
	private JwtUtils jwtUtils;
	
	@Autowired
	public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder encoder, UserRepository userRepository, RoleRepository roleRepository, RefreshTokenService refreshTokenService, JwtUtils jwtUtils) {
		this.authenticationManager = authenticationManager;
		this.encoder = encoder;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.refreshTokenService = refreshTokenService;
		this.jwtUtils = jwtUtils;
	}
	
	@Override
	public User createUser(RegistrationRequest registrationRequest) {
		if (Boolean.TRUE.equals(userRepository.existsByUsername(registrationRequest.getUsername()))) {
			throw new UserRegistrationException(String.format("Username %s is already taken!", registrationRequest.getUsername()));
		}
		
		if (Boolean.TRUE.equals(userRepository.existsByEmail(registrationRequest.getEmail()))) {
			throw new UserRegistrationException(String.format("Email %s is already in use!", registrationRequest.getEmail()));
		}
		
		User user = new User(registrationRequest.getUsername(), registrationRequest.getEmail(), encoder.encode(registrationRequest.getPassword()));
		
		Set<String> strRoles = registrationRequest.getRole();
		Set<Role> roles = new HashSet<>();
		
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
					.orElseThrow(() -> new UserRegistrationException(ROLE_NOT_FOUND_ERROR_MESSAGE));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				UserRole userRole = null;
				switch (role) {
					case "admin":
						userRole = UserRole.ROLE_ADMIN;
						break;
					case "user":
						userRole = UserRole.ROLE_USER;
						break;
					default:
							throw new UserRegistrationException(ROLE_NOT_FOUND_ERROR_MESSAGE);
				}
				
				roleRepository.findByName(userRole)
					.map(roles::add)
					.orElseThrow(() -> new UserRegistrationException(ROLE_NOT_FOUND_ERROR_MESSAGE));
			});
		}
		user.setRoles(roles);
		user = userRepository.save(user);
		return user;
	}

	@Override
	public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		
		RefreshToken resfreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		return new AuthenticationResponse(jwt, resfreshToken.getToken());
	}

	@Override
	public String renewToken(String refreshToken) {
		return refreshTokenService.findByToken(refreshToken)
	        .map(refreshTokenService::verifyExpiration)
	        .map(RefreshToken::getUser)
	        .map(user -> jwtUtils.generateJwtToken(user))
	        .orElseThrow(() -> new TokenRefreshException(String.format("[%s] is an invalid Refresh Token!", refreshToken)));
	}

	@Transactional
	@Override
	public void deleteUser(String userId) {
		if (!userRepository.existsById(userId)) {
			throw new UsernameNotFoundException(String.format("No user found with userId : %s", userId));
		}
		userRepository.deleteById(userId);	
	}
}
