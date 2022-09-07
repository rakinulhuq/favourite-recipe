package com.abnamro.nl.favouriterecipe.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.dto.request.CreateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.SearchRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.UpdateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.response.SearchRecipeResponse;
import com.abnamro.nl.favouriterecipe.service.RecipeService;

@RestController
@RequestMapping(path = "/recipe")
public class RecipeController {

	private RecipeService recipeService;
	
	@Autowired
	public RecipeController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}
	
	@PostMapping(path = "/create")
	public ResponseEntity<RecipeDto> createRecipe(@Valid @RequestBody CreateRecipeRequest createRecipeRequest) {
		RecipeDto recipeDto = recipeService.createRecipe(createRecipeRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(recipeDto);
	}
	
	@PutMapping(path = "/update")
	public ResponseEntity<RecipeDto> updateRecipe(@Valid @RequestBody UpdateRecipeRequest updateRecipeRequest) {
		RecipeDto recipeDto = recipeService.updateRecipe(updateRecipeRequest);
		return ResponseEntity.ok(recipeDto);
	}
	
	@DeleteMapping(path = "/delete/{recipeId}")
	public ResponseEntity<Void> deleteRecipe(@NotEmpty @PathVariable(name = "recipeId") String recipeId) {
		recipeService.deleteRecipe(recipeId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(path = "/find/{recipeId}")
	public ResponseEntity<RecipeDto> findRecipe(@NotEmpty @PathVariable(name = "recipeId") String recipeId) {
		RecipeDto recipeDto = recipeService.getRecipeById(recipeId);
		return ResponseEntity.ok(recipeDto);
	}
	
	@PostMapping(path = "/search")
	public ResponseEntity<SearchRecipeResponse> search(@Valid @RequestBody SearchRecipeRequest searchRecipeRequest) {
		SearchRecipeResponse searchRecipeResponse = recipeService.searchRecipes(searchRecipeRequest);
		return ResponseEntity.ok(searchRecipeResponse);
	}
}
