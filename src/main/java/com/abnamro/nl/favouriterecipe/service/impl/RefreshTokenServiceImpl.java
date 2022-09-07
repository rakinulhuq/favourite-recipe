package com.abnamro.nl.favouriterecipe.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abnamro.nl.favouriterecipe.exception.TokenRefreshException;
import com.abnamro.nl.favouriterecipe.model.auth.RefreshToken;
import com.abnamro.nl.favouriterecipe.model.auth.User;
import com.abnamro.nl.favouriterecipe.repository.RefreshTokenRepository;
import com.abnamro.nl.favouriterecipe.repository.UserRepository;
import com.abnamro.nl.favouriterecipe.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	@Value("${fr.refreshTokenExpiration}")
	private int refreshTokenDurationMs;
	
	private RefreshTokenRepository refreshTokenRepository;
	
	private UserRepository userRepository;
	
	@Autowired
	public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public Optional<RefreshToken> findByToken(String token) {
	    return refreshTokenRepository.findByToken(token);
	}
	
	@Override
	public RefreshToken createRefreshToken(String userId) {
		RefreshToken refreshToken = new RefreshToken();
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			refreshToken.setUser(user.get());
		    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		    refreshToken.setToken(UUID.randomUUID().toString());
		    refreshToken = refreshTokenRepository.save(refreshToken);
		}
	    
	    return refreshToken;
	}

	@Override
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(String.format("Refresh token [%s] was expired. Please make a new signin request!", token.getToken()));
	    }
	    return token;
	}

	@Transactional
	@Override
	public void deleteByUserId(String userId) {
		userRepository.findById(userId)
		.map(user -> refreshTokenRepository.deleteByUser(user))
		.orElseThrow(() -> new UsernameNotFoundException(userId));
	}

}
