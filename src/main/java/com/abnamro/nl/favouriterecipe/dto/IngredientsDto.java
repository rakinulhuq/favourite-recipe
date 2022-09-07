package com.abnamro.nl.favouriterecipe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IngredientsDto {
	private boolean exclude;
	private List<String> ingredients;
}
