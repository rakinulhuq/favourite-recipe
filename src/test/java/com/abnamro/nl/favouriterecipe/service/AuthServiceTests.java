package com.abnamro.nl.favouriterecipe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.abnamro.nl.favouriterecipe.Constants;
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
import com.abnamro.nl.favouriterecipe.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private PasswordEncoder encoder;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private RefreshTokenService refreshTokenService;
	
	@Mock
	private JwtUtils jwtUtils;
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	@DisplayName("Test create user with no roles provided")
	@Test
	public void createUser_successfulRequest_noRole() {
		//given
		Role role = new Role(Constants.USER_ROLE_ID, UserRole.ROLE_USER);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		User user = new User(Constants.USERNAME, Constants.EMAIL, Constants.PASSWORD, roles);
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, null, Constants.PASSWORD);
		given(userRepository.existsByUsername(registrationRequest.getUsername())).willReturn(false);
		given(userRepository.existsByEmail(registrationRequest.getEmail())).willReturn(false);
		given(roleRepository.findByName(UserRole.ROLE_USER)).willReturn(Optional.of(role));
		given(userRepository.save(any(User.class))).willReturn(user);

		//when
		User savedUser = authService.createUser(registrationRequest);
		//then
		assertNotNull(savedUser);
		assertEquals(Constants.USERNAME, savedUser.getUsername());
		assertEquals(Constants.EMAIL, savedUser.getEmail());
		assertFalse(savedUser.getRoles().isEmpty());
		assertEquals(1, savedUser.getRoles().size());
		assertEquals(role.getName(), savedUser.getRoles().iterator().next().getName());
	}
	
	@DisplayName("Test create user with single role provided")
	@Test
	public void createUser_successfulRequest_singleRole() {
		//given
		Role role = new Role(Constants.ADMIN_ROLE_ID, UserRole.ROLE_ADMIN);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		
		Set<String> roleNames = new HashSet<>();
		roleNames.add("admin");
		
		User user = new User(Constants.USERNAME, Constants.EMAIL, Constants.PASSWORD, roles);
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, roleNames, Constants.PASSWORD);
		given(userRepository.existsByUsername(registrationRequest.getUsername())).willReturn(false);
		given(userRepository.existsByEmail(registrationRequest.getEmail())).willReturn(false);
		given(roleRepository.findByName(UserRole.ROLE_ADMIN)).willReturn(Optional.of(role));
		given(userRepository.save(any(User.class))).willReturn(user);

		//when
		User savedUser = authService.createUser(registrationRequest);
		//then
		assertNotNull(savedUser);
		assertEquals(Constants.USERNAME, savedUser.getUsername());
		assertEquals(Constants.EMAIL, savedUser.getEmail());
		assertFalse(savedUser.getRoles().isEmpty());
		assertEquals(1, savedUser.getRoles().size());
		assertEquals(role.getName(), savedUser.getRoles().iterator().next().getName());
	}
	
	@DisplayName("Test create user with multiple roles provided")
	@Test
	public void createUser_successfulRequest_multipleRoles() {
		//given
		Role userRole = new Role(Constants.USER_ROLE_ID, UserRole.ROLE_USER);
		Role adminRole = new Role(Constants.ADMIN_ROLE_ID, UserRole.ROLE_ADMIN);
		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		roles.add(adminRole);
		
		Set<String> roleNames = new HashSet<>();
		roleNames.add("user");
		roleNames.add("admin");
		
		User user = new User(Constants.USERNAME, Constants.EMAIL, Constants.PASSWORD, roles);
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, roleNames, Constants.PASSWORD);
		given(userRepository.existsByUsername(registrationRequest.getUsername())).willReturn(false);
		given(userRepository.existsByEmail(registrationRequest.getEmail())).willReturn(false);
		given(roleRepository.findByName(UserRole.ROLE_USER)).willReturn(Optional.of(userRole));
		given(roleRepository.findByName(UserRole.ROLE_ADMIN)).willReturn(Optional.of(adminRole));
		given(userRepository.save(any(User.class))).willReturn(user);

		//when
		User savedUser = authService.createUser(registrationRequest);
		//then
		assertNotNull(savedUser);
		assertEquals(Constants.USERNAME, savedUser.getUsername());
		assertEquals(Constants.EMAIL, savedUser.getEmail());
		assertFalse(savedUser.getRoles().isEmpty());
		assertEquals(2, savedUser.getRoles().size());
	}
	
	@DisplayName("Test create user with single role provided")
	@Test
	public void createUser_singleRole_roleNotMatch() {
		//given
		Role role = new Role(Constants.ADMIN_ROLE_ID, UserRole.ROLE_ADMIN);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		
		Set<String> roleNames = new HashSet<>();
		roleNames.add("test");
		
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, roleNames, Constants.PASSWORD);
		given(userRepository.existsByUsername(registrationRequest.getUsername())).willReturn(false);
		given(userRepository.existsByEmail(registrationRequest.getEmail())).willReturn(false);

		//when
		assertThrows(UserRegistrationException.class, () -> authService.createUser(registrationRequest));
		//then
        verify(userRepository, never()).save(any(User.class));
	}
	
	@DisplayName("Test create user with single role provided")
	@Test
	public void createUser_singleRole_roleNotFound() {
		//given
		Role role = new Role(Constants.ADMIN_ROLE_ID, UserRole.ROLE_ADMIN);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		
		Set<String> roleNames = new HashSet<>();
		roleNames.add("admin");
		
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, roleNames, Constants.PASSWORD);
		given(userRepository.existsByUsername(registrationRequest.getUsername())).willReturn(false);
		given(userRepository.existsByEmail(registrationRequest.getEmail())).willReturn(false);
		given(roleRepository.findByName(UserRole.ROLE_ADMIN)).willReturn(Optional.empty());

		//when
		assertThrows(UserRegistrationException.class, () -> authService.createUser(registrationRequest));
		//then
        verify(userRepository, never()).save(any(User.class));
	}
	
	@DisplayName("Test create user with existing username")
	@Test
	public void createUser_existingUsername() {
		//given
		Set<String> roles = new HashSet<>();
		roles.add(UserRole.ROLE_USER.name());
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, roles, Constants.PASSWORD);
		given(userRepository.existsByUsername(Constants.USERNAME)).willReturn(true);
		//when
		assertThrows(UserRegistrationException.class, () -> authService.createUser(registrationRequest));
		//then
        verify(roleRepository, never()).findByName(UserRole.ROLE_USER);
        verify(userRepository, never()).save(any(User.class));
	}
	
	@DisplayName("Test create user with existing email")
	@Test
	public void createUser_existingEmail() {
		//given
		Set<String> roles = new HashSet<>();
		roles.add(UserRole.ROLE_USER.name());
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, roles, Constants.PASSWORD);
		given(userRepository.existsByUsername(Constants.USERNAME)).willReturn(false);
		given(userRepository.existsByEmail(Constants.EMAIL)).willReturn(true);
		//when
		assertThrows(UserRegistrationException.class, () -> authService.createUser(registrationRequest));
		//then
        verify(roleRepository, never()).findByName(UserRole.ROLE_USER);
        verify(userRepository, never()).save(any(User.class));
	}
	
	@DisplayName("Test authenticate user for valid user")
	@Test
	public void authenticateUser_validRequest() {
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(Constants.USERNAME, Constants.PASSWORD);
		UserDetailsImpl principal = new UserDetailsImpl(Constants.USER_ID, Constants.USERNAME, Constants.EMAIL, Constants.PASSWORD, Constants.AUTHORITIES);
		User user = new User(Constants.USERNAME, Constants.PASSWORD, Constants.EMAIL);
		RefreshToken refreshToken = new RefreshToken(user, Constants.REFRESH_TOKEN, Instant.now());
		
		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, Constants.AUTHORITIES);

		//given
		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
		given(jwtUtils.generateJwtToken(authentication)).willReturn(Constants.ACCESS_TOKEN);
		given(refreshTokenService.createRefreshToken(principal.getId())).willReturn(refreshToken);
		//when
		AuthenticationResponse authenticationResponse = authService.authenticateUser(authenticationRequest);
		//then
		assertNotNull(authenticationResponse);
		assertEquals(Constants.ACCESS_TOKEN, authenticationResponse.getAccessToken());
		assertEquals(Constants.REFRESH_TOKEN, authenticationResponse.getRefreshToken());
	}
	
	@DisplayName("Test authenticate user for invalid user")
	@Test
	public void authenticateUser_invalidRequest() {
		//given
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(Constants.USERNAME, Constants.PASSWORD);
		//when
		assertThrows(NullPointerException.class, () -> authService.authenticateUser(authenticationRequest));
		//then
        verify(jwtUtils, never()).generateJwtToken(any(User.class));
        verify(refreshTokenService, never()).createRefreshToken(any(String.class));
	}
	
	@DisplayName("Test authenticate user for empty request")
	@Test
	public void authenticateUser_emptyRequest() {
		//given
		AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		//when
		assertThrows(NullPointerException.class, () -> authService.authenticateUser(authenticationRequest));
		//then
        verify(jwtUtils, never()).generateJwtToken(any(User.class));
        verify(refreshTokenService, never()).createRefreshToken(any(String.class));
	}
	
	@DisplayName("Test authenticate user for null request")
	@Test
	public void authenticateUser_nullRequest() {
		//when
		assertThrows(NullPointerException.class, () -> authService.authenticateUser(null));
		//then
        verify(jwtUtils, never()).generateJwtToken(any(User.class));
        verify(refreshTokenService, never()).createRefreshToken(any(String.class));
	}
	
	@DisplayName("Test renew token for valid refresh token")
	@Test
	public void renewToken_validRefreshToken() {
		User user = new User(Constants.USERNAME, Constants.PASSWORD, Constants.EMAIL);
		RefreshToken refreshToken = new RefreshToken(user, Constants.REFRESH_TOKEN, Instant.now());
		//given
		given(refreshTokenService.findByToken(Constants.REFRESH_TOKEN)).willReturn(Optional.of(refreshToken));
		given(refreshTokenService.verifyExpiration(refreshToken)).willReturn(refreshToken);
		given(jwtUtils.generateJwtToken(user)).willReturn(Constants.ACCESS_TOKEN);
		//when
		String accessToken = authService.renewToken(Constants.REFRESH_TOKEN);
		//then
		assertEquals(Constants.ACCESS_TOKEN, accessToken);
	}
	
	@DisplayName("Test renew token for invalid refresh token")
	@Test
	public void renewToken_inValidRefreshToken() {
		//given
		given(refreshTokenService.findByToken(Constants.REFRESH_TOKEN)).willReturn(Optional.empty());
		//when
		assertThrows(TokenRefreshException.class, () -> authService.renewToken(Constants.REFRESH_TOKEN));
		//then
        verify(refreshTokenService, never()).verifyExpiration(any(RefreshToken.class));
        verify(jwtUtils, never()).generateJwtToken(any(User.class));
	}
	
	@DisplayName("Test delete user for valid user ID")
	@Test
	public void deleteUserTest_validUserId() {
		//given
		given(userRepository.existsById(Constants.USER_ID)).willReturn(true);
		doNothing().when(userRepository).deleteById(isA(String.class));
		//when
		authService.deleteUser(Constants.USER_ID);
		//then
		verify(userRepository, times(1)).deleteById(Constants.USER_ID);
	}
	
	@DisplayName("Test delete user throws UsernameNotFoundException")
	@Test
	public void deleteUserTest_inValidUserId() {
		//given
		given(userRepository.existsById(Constants.USER_ID)).willReturn(false);
		//when
		assertThrows(UsernameNotFoundException.class, () -> authService.deleteUser(Constants.USER_ID));
		//then
        verify(userRepository, never()).deleteById(any(String.class));
	}
	
}
