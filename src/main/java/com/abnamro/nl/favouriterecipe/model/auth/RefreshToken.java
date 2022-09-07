package com.abnamro.nl.favouriterecipe.model.auth;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import com.abnamro.nl.favouriterecipe.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "refreshtokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken extends BaseEntity {
  
	private User user;
	
	private String token;
	
	private Instant expiryDate;
}
