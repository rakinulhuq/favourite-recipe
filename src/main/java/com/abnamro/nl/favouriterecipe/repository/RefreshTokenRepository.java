package com.abnamro.nl.favouriterecipe.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.abnamro.nl.favouriterecipe.model.auth.RefreshToken;
import com.abnamro.nl.favouriterecipe.model.auth.User;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String>{
    Optional<RefreshToken> findById(String id);
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
