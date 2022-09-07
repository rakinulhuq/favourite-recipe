package com.abnamro.nl.favouriterecipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class FavouriteRecipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FavouriteRecipeApplication.class, args);
	}
}
