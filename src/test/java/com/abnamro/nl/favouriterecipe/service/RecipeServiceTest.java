package com.abnamro.nl.favouriterecipe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abnamro.nl.favouriterecipe.Constants;
import com.abnamro.nl.favouriterecipe.configuration.AuthenticationFacade;
import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.dto.request.CreateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.UpdateRecipeRequest;
import com.abnamro.nl.favouriterecipe.exception.ResourceNotFoundException;
import com.abnamro.nl.favouriterecipe.mapper.RecipeMapper;
import com.abnamro.nl.favouriterecipe.model.Recipe;
import com.abnamro.nl.favouriterecipe.repository.RecipeRepository;
import com.abnamro.nl.favouriterecipe.service.impl.RecipeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

	@Mock
	private RecipeRepository recipeRepository;
	
	@Mock
	private RecipeMapper recipeMapper;
	
	@Mock
	private AuthenticationFacade authenticationFacade;
	
	@InjectMocks
	private RecipeServiceImpl recipeService;
	
	private Recipe recipe;
	private Recipe updatedRecipe;
	private RecipeDto recipeDto;
	
	@BeforeEach
	public void setup() {
		recipe = Recipe
				.builder()
				.name(Constants.RECIPE_ONE_NAME)
				.isVegetarian(true)
				.ingredients(Constants.RECIPE_ONE_INGREDIENTS)
				.instructions(Constants.RECIPE_ONE_INSTRUCTIONS)
				.build();
		
		updatedRecipe = Recipe
				.builder()
				.name(Constants.RECIPE_ONE_UPDATED_NAME)
				.isVegetarian(false)
				.ingredients(Constants.RECIPE_ONE_UPDATED_INGREDIENTS)
				.instructions(Constants.RECIPE_ONE_UPDATED_INSTRUCTIONS)
				.build();
		
		recipeDto = RecipeDto
				.builder()
				.id(Constants.RECIPE_ONE_ID)
				.name(Constants.RECIPE_ONE_NAME)
				.isVegetarian(true)
				.ingredients(Constants.RECIPE_ONE_INGREDIENTS)
				.instructions(Constants.RECIPE_ONE_INSTRUCTIONS)
				.build();
				
	}
	
	
	@DisplayName("Test to check delete recipe throws ResourceNotFoundException")
	@Test
	public void deleteRecipeByIdTest_throwsResourceNotFoundException() {
		//given
		given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
		given(recipeRepository.existsByIdAndCreatedBy(Constants.RECIPE_ONE_ID, Constants.USER_ID)).willReturn(false);
		//when
		assertThrows(ResourceNotFoundException.class, () -> recipeService.deleteRecipe(Constants.RECIPE_ONE_ID));
		//then
        verify(recipeRepository, never()).deleteById(Constants.RECIPE_ONE_ID);
        verify(recipeRepository, never()).delete(any(Recipe.class));
	}
	
	@DisplayName("Test to check get recipe returns non null recipe object with valid ID")
	@Test
	public void getRecipeByIdTest_validId_notNull() {
		//given
		given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
		given(recipeRepository.findByIdAndCreatedBy(Constants.RECIPE_ONE_ID, Constants.USER_ID)).willReturn(Optional.of(recipe));
		given(recipeMapper.toRecipeDto(recipe)).willReturn(recipeDto);
		//when
		RecipeDto foundRecipeDto = recipeService.getRecipeById(Constants.RECIPE_ONE_ID);
		//then
		assertThat(foundRecipeDto).isNotNull();
	}
	
	@DisplayName("Test to check get recipe returns expected recipe object with valid ID")
	@Test
	public void getRecipeByIdTest_validId_equalObject() {
		//given
		given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
		given(recipeRepository.findByIdAndCreatedBy(Constants.RECIPE_ONE_ID, Constants.USER_ID)).willReturn(Optional.of(recipe));
		given(recipeMapper.toRecipeDto(recipe)).willReturn(recipeDto);
		//when
		RecipeDto foundRecipeDto = recipeService.getRecipeById(Constants.RECIPE_ONE_ID);
		//then
		assertThat(foundRecipeDto).isNotNull();
		//assertEquals(foundRecipe, recipe);
	}
	
	@DisplayName("Test to check get recipe throws ResourceNotFoundException with invalid Id")
	@Test
	public void getRecipeByIdTest_throwsResourceNotFoundException() {
		//given
		given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
		given(recipeRepository.findByIdAndCreatedBy(Constants.RECIPE_ONE_ID, Constants.USER_ID)).willReturn(Optional.empty());
		//when
		assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(Constants.RECIPE_ONE_ID));
		//then
        verify(recipeRepository, never()).deleteById(Constants.RECIPE_ONE_ID);
        verify(recipeRepository, never()).delete(any(Recipe.class));
	}
	
	@DisplayName("Test to check recipe exists by Id for valid Id")
	@Test
	public void recipeExistsByIdTest_validId() {
		//given
		given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
		given(recipeRepository.existsByIdAndCreatedBy(Constants.RECIPE_ONE_ID, Constants.USER_ID)).willReturn(true);
		//when
		boolean recipeExists = recipeService.recipeExistsById(Constants.RECIPE_ONE_ID);
		//then
        assertTrue(recipeExists);
	}
	
	@DisplayName("Test to check recipe exists by Id for invalid Id")
	@Test
	public void recipeExistsByIdTest_inValidId() {
		recipeExistsByIdTest("123");
	}
	
	@DisplayName("Test to check recipe exists by Id for invalid Id")
	@Test
	public void recipeExistsByIdTest_emptyId() {
		recipeExistsByIdTest("");
	}
	
	@DisplayName("Test to check recipe exists by Id for null Id")
	@Test
	public void recipeExistsByIdTest_nullId() {
		recipeExistsByIdTest(null);
	}
	
	private void recipeExistsByIdTest(String recipeId) {
		//when
		boolean recipeExists = recipeService.recipeExistsById(recipeId);
		//then
        assertFalse(recipeExists);
	}
	
	@Nested
	class RecipeServiceNestedTests {
		
		private CreateRecipeRequest createRecipeRequest;
		private UpdateRecipeRequest updateRecipeRequest;
		private RecipeDto recipeDto;
		private RecipeDto updatedRecipeDto;
		
		@BeforeEach()
		public void createRecipeRequestSetup() {
			createRecipeRequest = CreateRecipeRequest
									.builder()
									.name(Constants.RECIPE_ONE_NAME)
									.isVegetarian(true)
									.ingredients(Constants.RECIPE_ONE_INGREDIENTS)
									.instructions(Constants.RECIPE_ONE_INSTRUCTIONS)
									.build();
			
			updateRecipeRequest = UpdateRecipeRequest
					.builder()
					.id(Constants.RECIPE_ONE_ID)
					.name(Constants.RECIPE_ONE_UPDATED_NAME)
					.isVegetarian(false)
					.ingredients(Constants.RECIPE_ONE_UPDATED_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_UPDATED_INSTRUCTIONS)
					.build();
			
			recipeDto = RecipeDto
					.builder()
					.id(Constants.RECIPE_ONE_ID)
					.name(Constants.RECIPE_ONE_NAME)
					.isVegetarian(true)
					.ingredients(Constants.RECIPE_ONE_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_INSTRUCTIONS)
					.build();
			
			updatedRecipeDto = RecipeDto
					.builder()
					.id(Constants.RECIPE_ONE_ID)
					.name(Constants.RECIPE_ONE_UPDATED_NAME)
					.isVegetarian(false)
					.ingredients(Constants.RECIPE_ONE_UPDATED_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_UPDATED_INSTRUCTIONS)
					.build();
		}
		
		@DisplayName("Test to check created recipeDto from saved recipe is not null")
		@Test
		public void createRecipeTest_notNull() {
			//given
			given(recipeMapper.fromCreateRecipeRequest(createRecipeRequest)).willReturn(recipe);
			given(recipeRepository.save(recipe)).willReturn(recipe);
			given(recipeMapper.toRecipeDto(recipe)).willReturn(recipeDto);
			//when
			RecipeDto savedRecipeDto = recipeService.createRecipe(createRecipeRequest);
			//then
			assertThat(savedRecipeDto).isNotNull();
		}
		
		@DisplayName("Test to check created recipeDto from saved recipe is same")
		@Test
		public void createRecipeTest_equalObject() {
			//given
			given(recipeMapper.fromCreateRecipeRequest(createRecipeRequest)).willReturn(recipe);
			given(recipeRepository.save(recipe)).willReturn(recipe);
			given(recipeMapper.toRecipeDto(recipe)).willReturn(recipeDto);
			//when
			RecipeDto savedRecipeDto = recipeService.createRecipe(createRecipeRequest);
			//then
			assertEquals(savedRecipeDto, recipeDto);
		}
		
		@DisplayName("Test to check created recipeDto from updated recipe is not null")
		@Test
		public void updateRecipeTest_notNull() throws Exception{
			//given
			given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
			given(recipeRepository.existsByIdAndCreatedBy(updateRecipeRequest.getId(), Constants.USER_ID)).willReturn(true);
			given(recipeMapper.fromUpdateRecipeRequest(updateRecipeRequest)).willReturn(updatedRecipe);
			given(recipeRepository.save(updatedRecipe)).willReturn(updatedRecipe);
			given(recipeMapper.toRecipeDto(updatedRecipe)).willReturn(updatedRecipeDto);
			//when
			RecipeDto savedRecipeDto = recipeService.updateRecipe(updateRecipeRequest);
			//then
			assertThat(savedRecipeDto).isNotNull();
			
		}
		
		@DisplayName("Test to check created recipeDto from updated recipe is same")
		@Test
		public void updateRecipeTest_equalObjects() throws Exception {
			//given
			given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
			given(recipeRepository.existsByIdAndCreatedBy(updateRecipeRequest.getId(), Constants.USER_ID)).willReturn(true);
			given(recipeMapper.fromUpdateRecipeRequest(updateRecipeRequest)).willReturn(updatedRecipe);
			given(recipeRepository.save(updatedRecipe)).willReturn(updatedRecipe);
			given(recipeMapper.toRecipeDto(updatedRecipe)).willReturn(updatedRecipeDto);
			//when
			RecipeDto savedRecipeDto = recipeService.updateRecipe(updateRecipeRequest);
			//then
			assertEquals(savedRecipeDto, updatedRecipeDto);
		}
		
		@DisplayName("Test to check update recipe throws ResourceNotFoundException")
		@Test
		public void updateRecipeTest_throwResourceNotFoundException() {
			//given
			given(authenticationFacade.getLoggedInUserId()).willReturn(Constants.USER_ID);
			given(recipeRepository.existsByIdAndCreatedBy(updateRecipeRequest.getId(), Constants.USER_ID)).willReturn(false);
			//when
			assertThrows(ResourceNotFoundException.class, () -> recipeService.updateRecipe(updateRecipeRequest));
			//then
	        verify(recipeRepository, never()).save(any(Recipe.class));
		}
	}	
}
