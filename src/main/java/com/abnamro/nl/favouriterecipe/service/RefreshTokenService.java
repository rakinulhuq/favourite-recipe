package com.abnamro.nl.favouriterecipe.service;

import java.util.Optional;

import com.abnamro.nl.favouriterecipe.model.auth.RefreshToken;

public interface RefreshTokenService {
	Optional<RefreshToken> findByToken(String token);
	RefreshToken createRefreshToken(String userId);
	RefreshToken verifyExpiration(RefreshToken token);
	void deleteByUserId(String userId);
}
