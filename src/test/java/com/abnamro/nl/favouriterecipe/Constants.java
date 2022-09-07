package com.abnamro.nl.favouriterecipe;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Constants {

	public static final String USER_ID = "4676edf2-3460-470c-b1b2-5f7ae5af2fae";
	public static final String USERNAME = "johny_bravo";
	public static final String PASSWORD = "JohN#3134!";
	public static final String EMAIL = "johny@dummyrecipe.com";
	public static final String ACCESS_TOKEN = "ddwadwqer12312d!23%^&%$%^34563453";
	public static final String REFRESH_TOKEN = "ddwadwqer12312d!23%^&%$%^34563453";
	public static final String INVALID_REFRESH_TOKEN = "ddwadwqer12312d!23%^&%$%^34563453";
	public static final List<GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority("ROLE_USER"));;
	public static final String USER_ROLE_ID = "c7250c9e-9393-4155-8a4a-a8eadbe9943f";
	public static final String ADMIN_ROLE_ID = "f8c13d3e-19fe-440c-bfe8-a0837a5eca59";
	
	public final static String RECIPE_ONE_ID = "63c065d1-3460-470c-a91d-5f7ae5af2fae";
	public final static String RECIPE_ONE_NAME = "Mojito";
	public final static String RECIPE_ONE_UPDATED_NAME = "Virgin Mojito";
	public final static String RECIPE_ONE_CREATED_BY = "2632edf2-b7e1-4d62-904a-0d9cb44d1327";
	public final static String RECIPE_ONE_UPDATED_BY = "37e0cfda-b603-4977-b1b2-81d490c2c039";
	public final static String RECIPE_ONE_UPDATED_UPDATED_BY = "035c22ef-2639-464a-bf66-997c6345f153";
	public final static LocalDateTime RECIPE_ONE_CREATED_DATE = LocalDateTime.now();
	public final static LocalDateTime RECIPE_ONE_LAST_UPDATED_DATE = LocalDateTime.now();
	public final static LocalDateTime RECIPE_ONE_UPDATED_LAST_UPDATED_DATE = LocalDateTime.now().plusMinutes(5);
	public final static List<String> RECIPE_ONE_INGREDIENTS = Arrays.asList("ice", "lemon", "sugar", "soda water", "mint", "white rum");
	public final static List<String> RECIPE_ONE_UPDATED_INGREDIENTS = Arrays.asList("ice", "lemon", "sugar", "sprite", "mint");
	public final static String RECIPE_ONE_INSTRUCTIONS = "Mix lemon, mint, sugar and muddle them together, then add sugar, soda water and white rum. Enjoy!";
	public final static String RECIPE_ONE_UPDATED_INSTRUCTIONS = "Mix lemon, mint, sugar and muddle them together, then add sugar and sprite. Enjoy!";
	
}
