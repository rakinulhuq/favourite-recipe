package com.abnamro.nl.favouriterecipe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.abnamro.nl.favouriterecipe.model.Recipe;
import com.abnamro.nl.favouriterecipe.model.auth.User;
import com.abnamro.nl.favouriterecipe.service.AuthService;
import com.abnamro.nl.favouriterecipe.service.RecipeService;
import com.abnamro.nl.favouriterecipe.service.UserService;
import com.abnamro.nl.favouriterecipe.Constants;
import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.dto.request.AuthenticationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.CreateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.RegistrationRequest;
import com.abnamro.nl.favouriterecipe.dto.request.SearchRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.request.UpdateRecipeRequest;
import com.abnamro.nl.favouriterecipe.dto.response.AuthenticationResponse;
import com.abnamro.nl.favouriterecipe.exception.ExceptionResponse;
import com.abnamro.nl.favouriterecipe.mapper.RecipeMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class RecipeControllerIT {
	
	public static final String RECIPE_BASE_URI = "/recipe";

	private HttpHeaders headers = new HttpHeaders();

	private User user;
	
	@LocalServerPort
    private int port;
	
	private AuthService authService;
	private RecipeService recipeService;
	private RecipeMapper recipeMapper;
	private TestRestTemplate restTemplate;
	private UserService userService;
	
	@Autowired
	public RecipeControllerIT (AuthService authService, RecipeService recipeService, RecipeMapper recipeMapper, TestRestTemplate restTemplate, UserService userService) {
		this.authService = authService;
		this.recipeService = recipeService;
		this.recipeMapper = recipeMapper;
		this.restTemplate = restTemplate;
		this.userService = userService;
	}
	
	@BeforeAll
	public void init() {
		userService.deleteUserIfExists(Constants.USERNAME);
		user = authService.createUser(new RegistrationRequest(Constants.USERNAME, Constants.EMAIL, null, Constants.PASSWORD));
	}
	
	@AfterAll
	public void destroy() {
		authService.deleteUser(user.getId());
	}
	
	@Test
	public void createRecipeTest_unauthorized() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
		          createURLWithPort("/create"), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void updateRecipeTest_unauthorized() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
		          createURLWithPort("/update"), HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void deleteRecipeTest_unauthorized() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
		          createURLWithPort(String.format("/delete/%s", Constants.RECIPE_ONE_ID)), HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void findRecipeTest_unauthorized() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
		          createURLWithPort(String.format("/find/%s", Constants.RECIPE_ONE_ID)), HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void searchRecipeTest_unauthorized() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
		          createURLWithPort("/search"), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	private String createURLWithPort(String uri) {
        return "http://localhost:" + port + RECIPE_BASE_URI + uri;
    }
	
	@Nested
	class RecipeControllerNestedIT {
		
		@BeforeEach
		public void tokenSetup() {
			AuthenticationResponse authenticationResponse = authService.authenticateUser(new AuthenticationRequest(Constants.USERNAME, Constants.PASSWORD));
			headers.setBearerAuth(authenticationResponse.getAccessToken());
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		
		@Test
		public void createRecipeTest_successfulRequest() {
			CreateRecipeRequest createRecipeRequest = createRecipeRequest();
			HttpEntity<CreateRecipeRequest> entity = new HttpEntity<CreateRecipeRequest>(createRecipeRequest, headers);
			ResponseEntity<RecipeDto> response = restTemplate.exchange(
			          createURLWithPort("/create"), HttpMethod.POST, entity, RecipeDto.class);

	        assertEquals(HttpStatus.CREATED, response.getStatusCode());
	        RecipeDto recipeDto = response.getBody();
	        assertNotNull(recipeDto);
	        recipeService.deleteRecipe(recipeDto.getId());
		}
		
		@Test
		public void createRecipeTest_failedRequest_nullRequestBody() {
			HttpEntity<CreateRecipeRequest> entity = new HttpEntity<CreateRecipeRequest>(null, headers);
			ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
			          createURLWithPort("/create"), HttpMethod.POST, entity, ExceptionResponse.class);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
		
		@Test
		public void updateRecipeTest_successfulRequest() {
			RecipeDto recipeDto = recipeService.createRecipe(createRecipeRequest());
			UpdateRecipeRequest updateRecipeRequest = updateRecipeRequest(recipeDto);
			HttpEntity<UpdateRecipeRequest> entity = new HttpEntity<UpdateRecipeRequest>(updateRecipeRequest, headers);
			ResponseEntity<RecipeDto> response = restTemplate.exchange(
			          createURLWithPort("/update"), HttpMethod.PUT, entity, RecipeDto.class);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        RecipeDto recipeDtoResponse = response.getBody();
	        assertNotNull(recipeDtoResponse);
	        recipeService.deleteRecipe(recipeDtoResponse.getId());
		}
		
		@Test
		public void updateRecipeTest_failedRequest_nullRequestBody() {
			HttpEntity<UpdateRecipeRequest> entity = new HttpEntity<UpdateRecipeRequest>(null, headers);
			ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
			          createURLWithPort("/update"), HttpMethod.PUT, entity, ExceptionResponse.class);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
		
		@Test
		public void findRecipeTest_successfulRequest() {
			RecipeDto recipeDto = recipeService.createRecipe(createRecipeRequest());
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			ResponseEntity<RecipeDto> response = restTemplate.exchange(
			          createURLWithPort(String.format("/find/%s", recipeDto.getId())), HttpMethod.GET, entity, RecipeDto.class);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        RecipeDto recipeDtoResponse = response.getBody();
	        assertNotNull(recipeDtoResponse);
	        recipeService.deleteRecipe(recipeDtoResponse.getId());
		}
		
		@Test
		public void findRecipeTest_failedRequest_emptyRecipeId() {
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			ResponseEntity<String> response = restTemplate.exchange(
			          createURLWithPort(String.format("/find/%s", "123")), HttpMethod.GET, entity, String.class);
	        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}
		
		@Test
		public void findRecipeTest_failedRequest_invalidRecipeId() {
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
			          createURLWithPort(String.format("/find/%s", "123")), HttpMethod.GET, entity, ExceptionResponse.class);
	        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	        assertEquals(HttpStatus.NOT_FOUND, response.getBody().getStatus());
	        assertEquals("No Recipe found with id : 123", response.getBody().getErrorMessage());
		}
		
		@Test
		public void deleteRecipeTest_successfulRequest() {
			RecipeDto recipeDto = recipeService.createRecipe(createRecipeRequest());
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			ResponseEntity<Void> response = restTemplate.exchange(
			          createURLWithPort(String.format("/delete/%s", recipeDto.getId())), HttpMethod.DELETE, entity, Void.class);
	        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		}
		
		@Test
		public void deleteRecipeTest_failedRequest_emptyRecipeId() {
			HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);
			ResponseEntity<String> response = restTemplate.exchange(
			          createURLWithPort(String.format("/delete/%s", "")), HttpMethod.DELETE, entity, String.class);
	        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		public void deleteRecipeTest_failedRequest_invalidRecipeId() {
			HttpEntity<CreateRecipeRequest> entity = new HttpEntity<CreateRecipeRequest>(null, headers);
			ResponseEntity<ExceptionResponse> response = restTemplate.exchange(
			          createURLWithPort(String.format("/delete/%s", "123")), HttpMethod.DELETE, entity, ExceptionResponse.class);
	        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	        assertEquals(HttpStatus.NOT_FOUND, response.getBody().getStatus());
	        assertEquals("No Recipe found with id : 123", response.getBody().getErrorMessage());
		}
		
		public void searchRecipeTest_successfulRequest() {
			Recipe recipe = createRecipe();
			CreateRecipeRequest createRecipeRequest = recipeMapper.toCreateRecipeRequest(recipe);
			HttpEntity<CreateRecipeRequest> entity = new HttpEntity<CreateRecipeRequest>(createRecipeRequest, headers);
			ResponseEntity<RecipeDto> response = restTemplate.exchange(
			          createURLWithPort("/create"), HttpMethod.POST, entity, RecipeDto.class);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
		}
		
		@Test
		public void searchRecipeTest_failedRequest_nullRequestBody() {
			HttpEntity<CreateRecipeRequest> entity = new HttpEntity<CreateRecipeRequest>(null, headers);
			ResponseEntity<String> response = restTemplate.exchange(
			          createURLWithPort("/search"), HttpMethod.POST, entity, String.class);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
		
		
		public void searchRecipeTest_authorized() {
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<SearchRecipeRequest> entity = new HttpEntity<SearchRecipeRequest>(null, headers);
			ResponseEntity<String> response = restTemplate.exchange(
			          createURLWithPort("/search"), HttpMethod.POST, entity, String.class);
			System.out.print(response.getBody());
			System.out.print(response.getStatusCode());
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
		
		public Recipe createRecipe() {
			Recipe recipe = Recipe
					.builder()
					.name(Constants.RECIPE_ONE_NAME)
					.isVegetarian(true)
					.ingredients(Constants.RECIPE_ONE_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_INSTRUCTIONS)
					.build();
			
			recipe.setId(Constants.RECIPE_ONE_ID);
			recipe.setCreatedBy(Constants.RECIPE_ONE_CREATED_BY);
			recipe.setUpdatedBy(Constants.RECIPE_ONE_UPDATED_BY);
			//recipe.setCreatedBy(Constants.RECIPE_ONE_CREATED_DATE);
			//recipe.setUpdatedBy(Constants.RECIPE_ONE_LAST_UPDATED_DATE);
			return recipe;
		}
		
		public Recipe updateRecipe() {
			Recipe recipe = Recipe
					.builder()
					.name(Constants.RECIPE_ONE_UPDATED_NAME)
					.isVegetarian(false)
					.ingredients(Constants.RECIPE_ONE_UPDATED_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_UPDATED_INSTRUCTIONS)
					.build();
			
			recipe.setId(Constants.RECIPE_ONE_ID);
			recipe.setCreatedBy(Constants.RECIPE_ONE_CREATED_BY);
			recipe.setUpdatedBy(Constants.RECIPE_ONE_UPDATED_UPDATED_BY);
			//recipe.setCreatedBy(Constants.RECIPE_ONE_CREATED_DATE);
			//recipe.setUpdatedBy(Constants.RECIPE_ONE_UPDATED_LAST_UPDATED_DATE);
			return recipe;
		}
		
		public CreateRecipeRequest createRecipeRequest() {
			return CreateRecipeRequest
					.builder()
					.name(Constants.RECIPE_ONE_NAME)
					.isVegetarian(false)
					.ingredients(Constants.RECIPE_ONE_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_INSTRUCTIONS)
					.build();
		}
		
		public UpdateRecipeRequest updateRecipeRequest(RecipeDto recipeDto) {
			return UpdateRecipeRequest
					.builder()
					.id(recipeDto.getId())
					.name(Constants.RECIPE_ONE_UPDATED_NAME)
					.isVegetarian(recipeDto.isVegetarian())
					.ingredients(Constants.RECIPE_ONE_UPDATED_INGREDIENTS)
					.instructions(Constants.RECIPE_ONE_UPDATED_INSTRUCTIONS)
					.build();
		}
	}
}
