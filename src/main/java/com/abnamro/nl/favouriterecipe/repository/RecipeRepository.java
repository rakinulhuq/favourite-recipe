package com.abnamro.nl.favouriterecipe.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.abnamro.nl.favouriterecipe.model.Recipe;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String>, CustomRecipeRepository {
	boolean existsByIdAndCreatedBy(String id, String createdBy);
	Optional<Recipe> findByIdAndCreatedBy(String id, String createdBy);
}
