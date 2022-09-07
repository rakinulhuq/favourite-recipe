package com.abnamro.nl.favouriterecipe.dto.request;

import org.springframework.data.domain.Sort.Direction;

import com.abnamro.nl.favouriterecipe.dto.IngredientsDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchRecipeRequest { 
	private Boolean isVegetarian;
	private Integer numberOfServings;
	private IngredientsDto ingredients;
	private String instructionKeyword;
	private int count;
	private int pageNumber;
	private Direction order = Direction.ASC;
	private String[] properties = new String[] {"name"};
}