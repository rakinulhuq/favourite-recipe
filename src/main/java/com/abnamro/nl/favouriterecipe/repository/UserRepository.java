package com.abnamro.nl.favouriterecipe.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.abnamro.nl.favouriterecipe.model.auth.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}
