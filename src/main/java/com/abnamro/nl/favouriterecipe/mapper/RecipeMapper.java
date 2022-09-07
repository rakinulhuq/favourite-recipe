package com.abnamro.nl.favouriterecipe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.dto.request.CreateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.UpdateRecipeRequest;
import com.abnamro.nl.favouriterecipe.model.Recipe;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
	
	RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);
	
	Recipe fromRecipeDto(RecipeDto recipeDto);
	RecipeDto toRecipeDto(Recipe recipe);
	
	Recipe fromCreateRecipeRequest(CreateRecipeRequest createRecipeRequest);
	CreateRecipeRequest toCreateRecipeRequest(Recipe recipe);
	
	
	Recipe fromUpdateRecipeRequest(UpdateRecipeRequest updateRecipeRequest);
	UpdateRecipeRequest toUpdateRecipeRequest(Recipe recipe);
}
