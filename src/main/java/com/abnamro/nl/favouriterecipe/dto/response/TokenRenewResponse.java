package com.abnamro.nl.favouriterecipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenRenewResponse {
	private String accessToken;
	private String refreshToken;
}
