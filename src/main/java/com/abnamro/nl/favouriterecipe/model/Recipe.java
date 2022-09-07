package com.abnamro.nl.favouriterecipe.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "recipes")
public class Recipe extends BaseEntity {
	private String name;
	private boolean isVegetarian;
	private int servings;
	private List<String> ingredients;
	private String instructions;
}
