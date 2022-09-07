package com.abnamro.nl.favouriterecipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import com.abnamro.nl.favouriterecipe.model.Recipe;

public interface CustomRecipeRepository {
	Page<Recipe> search(Query query, Pageable pageable);
}
