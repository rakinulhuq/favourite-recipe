package com.abnamro.nl.favouriterecipe.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.abnamro.nl.favouriterecipe.model.auth.Role;
import com.abnamro.nl.favouriterecipe.model.auth.UserRole;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(UserRole name);
}
