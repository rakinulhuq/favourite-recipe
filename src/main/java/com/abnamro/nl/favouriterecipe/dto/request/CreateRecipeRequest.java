package com.abnamro.nl.favouriterecipe.dto.request;

import java.util.List;

import com.abnamro.nl.favouriterecipe.model.Recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateRecipeRequest {
	private String name;
	private boolean isVegetarian;
	private int servings;
	private List<String> ingredients;
	private String instructions;
	
	public CreateRecipeRequest(Recipe recipe) {
		this.name = recipe.getName();
		this.isVegetarian = recipe.isVegetarian();
		this.servings = recipe.getServings();
		this.ingredients = recipe.getIngredients();
		this.instructions = recipe.getInstructions();
	}
}
