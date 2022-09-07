package com.abnamro.nl.favouriterecipe.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.abnamro.nl.favouriterecipe.dto.RecipeDto;
import com.abnamro.nl.favouriterecipe.model.Recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchRecipeResponse {
	private int totalPages;
    private long totalItems;
    private int currentPage;
    private boolean first;
    private boolean last;
    private int itemsPerPage;
    private int pageSize;
	private List<RecipeDto> recipes;
	
	public SearchRecipeResponse(Page<Recipe> page) {
		first = page.isFirst();
		last = page.isLast();
		currentPage = page.getNumber() + 1;
		pageSize = page.getSize();
		totalPages = page.getTotalPages();
        totalItems = page.getTotalElements();
        itemsPerPage = page.getNumberOfElements();
        recipes = page.getContent().stream().map(RecipeDto::new).toList();
    }
}
