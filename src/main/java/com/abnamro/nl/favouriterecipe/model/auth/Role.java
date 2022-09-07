package com.abnamro.nl.favouriterecipe.model.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {
	@Id
	private String id;
	private UserRole name;
}
