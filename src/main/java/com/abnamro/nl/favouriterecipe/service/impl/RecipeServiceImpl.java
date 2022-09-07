package com.abnamro.nl.favouriterecipe.service.impl;

import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.abnamro.nl.favouriterecipe.configuration.AuthenticationFacade;
import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.dto.request.CreateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.SearchRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.UpdateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.response.SearchRecipeResponse;
import com.abnamro.nl.favouriterecipe.exception.ResourceNotFoundException;
import com.abnamro.nl.favouriterecipe.mapper.RecipeMapper;
import com.abnamro.nl.favouriterecipe.model.Recipe;
import com.abnamro.nl.favouriterecipe.repository.RecipeRepository;
import com.abnamro.nl.favouriterecipe.service.RecipeService;

@Service
public class RecipeServiceImpl implements RecipeService {

	private static final String NO_RECIPE_FOUND_EXCEPTION_MESSAGE = "No Recipe found with id : %s";
	
	private RecipeRepository recipeRepository;
	
	private RecipeMapper recipeMapper;
	
	private AuthenticationFacade authenticationFacade; 
	
	@Autowired
	public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeMapper recipeMapper, AuthenticationFacade authenticationFacade) {
		this.recipeRepository = recipeRepository;
		this.recipeMapper = recipeMapper;
		this.authenticationFacade = authenticationFacade;
	}

	@Override
	public boolean recipeExistsById(String recipeId) {
		return recipeRepository.existsByIdAndCreatedBy(recipeId, authenticationFacade.getLoggedInUserId());
	}
	
	@Override
	public RecipeDto getRecipeById(String recipeId) {
		return recipeMapper.toRecipeDto(findRecipeById(recipeId));
	}
	
	@Override
	public RecipeDto createRecipe(CreateRecipeRequest createRecipeRequest) {
		Recipe recipe = recipeMapper.fromCreateRecipeRequest(createRecipeRequest);
		recipe = recipeRepository.save(recipe);
		return recipeMapper.toRecipeDto(recipe);
	}

	@Override
	public RecipeDto updateRecipe(UpdateRecipeRequest updateRecipeRequest) {
		if (!recipeExistsById(updateRecipeRequest.getId())) {
			throw new ResourceNotFoundException(getNoRecipeFoundExceptionMessage(updateRecipeRequest.getId()));
		}
		Recipe recipe = recipeMapper.fromUpdateRecipeRequest(updateRecipeRequest);
		recipe = recipeRepository.save(recipe);
		return recipeMapper.toRecipeDto(recipe);
	}
	
	@Override
	public void deleteRecipe(String recipeId) {
		if (!recipeExistsById(recipeId)) {
			throw new ResourceNotFoundException(getNoRecipeFoundExceptionMessage(recipeId)); 
		}
		recipeRepository.deleteById(recipeId);	
	}

	@Override
	public SearchRecipeResponse searchRecipes(SearchRecipeRequest searchRecipeRequest) {
		int limit = Math.max(20, searchRecipeRequest.getCount());
		int pageNumber = (searchRecipeRequest.getPageNumber() <= 0) ? 0 : searchRecipeRequest.getPageNumber();
		Sort sort = Sort.by(searchRecipeRequest.getOrder(), searchRecipeRequest.getProperties());
		Pageable pageable = PageRequest.of(pageNumber, limit, sort);	
		Query query = buildRecipeSearchQuery(searchRecipeRequest);
		Page<Recipe> recipePage = recipeRepository.search(query, pageable);
		return new SearchRecipeResponse(recipePage);
	}
	
	private Query buildRecipeSearchQuery(SearchRecipeRequest searchRecipeRequest) {
		Query query = new Query();
	    query.addCriteria(Criteria.where("createdBy").is(authenticationFacade.getLoggedInUser().getId()));

		if (searchRecipeRequest.getIsVegetarian() != null) {
			query.addCriteria(Criteria.where("isVegetarian").is(searchRecipeRequest.getIsVegetarian()));
		}
		if (searchRecipeRequest.getNumberOfServings() != null) {
			query.addCriteria(Criteria.where("servings").is(searchRecipeRequest.getNumberOfServings()));
		}
		if (searchRecipeRequest.getIngredients() != null 
				&& searchRecipeRequest.getIngredients().getIngredients() != null 
				&& !searchRecipeRequest.getIngredients().getIngredients().isEmpty()) {
			if (searchRecipeRequest.getIngredients().isExclude()) {
				query.addCriteria(Criteria.where("ingredients").nin(searchRecipeRequest.getNumberOfServings()));
			} else {
				query.addCriteria(Criteria.where("ingredients").all(searchRecipeRequest.getNumberOfServings()));
			}
		}
		if (Strings.isNotBlank(searchRecipeRequest.getInstructionKeyword())) {
			query.addCriteria(Criteria.where("instructions")
					.regex(Pattern.compile(".*" + searchRecipeRequest.getInstructionKeyword() + ".*", Pattern.CASE_INSENSITIVE)));
		}
		
		return query;
	}
	
	private Recipe findRecipeById(String recipeId) {
		return recipeRepository.findByIdAndCreatedBy(recipeId, authenticationFacade.getLoggedInUserId())
				.orElseThrow(() -> 
					new ResourceNotFoundException(getNoRecipeFoundExceptionMessage(recipeId)));
	}
	
	private String getNoRecipeFoundExceptionMessage(String recipeId) {
		return String.format(NO_RECIPE_FOUND_EXCEPTION_MESSAGE, recipeId);
	}
}
