package com.abnamro.nl.favouriterecipe.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.abnamro.nl.favouriterecipe.model.Recipe;
import com.abnamro.nl.favouriterecipe.repository.CustomRecipeRepository;

@Repository
public class CustomRecipeRepositoryImpl implements CustomRecipeRepository {

	private MongoTemplate mongoTemplate;
	
	@Autowired
	public CustomRecipeRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;	
	}
	
	public Page<Recipe> search(Query query, Pageable pageable) {
	    query.with(pageable);
	    List<Recipe> list = mongoTemplate.find(query, Recipe.class);
	    long count = mongoTemplate.count(query, Recipe.class);
	    return new PageImpl<>(list , pageable, count);
	}	
}
