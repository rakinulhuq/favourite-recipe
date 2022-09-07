package com.abnamro.nl.favouriterecipe.dto;

import java.util.List;

import com.abnamro.nl.favouriterecipe.model.Recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecipeDto {
	private String id;
	private String name;
	private boolean isVegetarian;
	private int servings;
	private List<String> ingredients;
	private String instructions;
	
	public RecipeDto(Recipe recipe) {
		this.id = recipe.getId();
		this.name = recipe.getName();
		this.isVegetarian = recipe.isVegetarian();
		this.servings = recipe.getServings();
		this.ingredients = recipe.getIngredients();
		this.instructions = recipe.getInstructions();
	}
}
