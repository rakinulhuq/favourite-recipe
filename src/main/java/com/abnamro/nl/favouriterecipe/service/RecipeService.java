package com.abnamro.nl.favouriterecipe.service;

import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.dto.request.CreateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.SearchRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.UpdateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.response.SearchRecipeResponse;

public interface RecipeService {
	boolean recipeExistsById(String recipeId);
	RecipeDto getRecipeById(String recipeId);
	RecipeDto createRecipe(CreateRecipeRequest createRecipeRequest);
	RecipeDto updateRecipe(UpdateRecipeRequest updateRecipeRequest);
	void deleteRecipe(String recipeId);
	SearchRecipeResponse searchRecipes(SearchRecipeRequest searchRecipeQuery);
}
