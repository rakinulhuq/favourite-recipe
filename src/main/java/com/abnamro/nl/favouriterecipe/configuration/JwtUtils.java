package com.abnamro.nl.favouriterecipe.configuration;

import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.abnamro.nl.favouriterecipe.model.UserDetailsImpl;
import com.abnamro.nl.favouriterecipe.model.auth.Role;
import com.abnamro.nl.favouriterecipe.model.auth.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

/**
 * 
 * @author Rakinul Huq
 * 
 * Utility class to generate token, validate token and extract claims from token.
 *
 */
@Component
public class JwtUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${fr.jwtsecret}")
	private String SECRET_KEY; 
		
	@Value("${fr.accessTokenExpiration}")
	private long accessTokenExpiration;
	
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return generateJwtToken(userPrincipal);
	}
  
	public String generateJwtToken(User user) {
		return generateJwtToken(user.getUsername(), getClaims(user.getUsername(), user.getEmail(), user.getRoles()));
	}
	
	public String generateJwtToken(UserDetailsImpl userPrincipal) {
		return generateJwtToken(userPrincipal.getUsername(), getClaims(userPrincipal));
	}
	
	public String generateJwtToken(String username, Claims claims) {
		String token = "";
		try {
			token = Jwts.builder()
					.setClaims(claims)
					.setSubject(username)
					.setIssuedAt(new Date())
					.setExpiration(new Date((new Date()).getTime() + accessTokenExpiration))
					.signWith(getKey(), signatureAlgorithm)
					.compact();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return token;
	}
	
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch(ExpiredJwtException e) {
			logger.error("JWT token expired: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		} catch (Exception e) {
			logger.error("Invalid JWT: {}", e.getMessage());
		}
	  
		return false;
	}
	
	private Claims getClaims(UserDetailsImpl userPrincipal) {
		return getClaims(userPrincipal.getUsername(), userPrincipal.getEmail(), userPrincipal.getAuthorities());
	}
	
	private Claims getClaims(String username, String email, Collection<? extends GrantedAuthority> authorities) {
		Claims claims = new DefaultClaims();
		claims.put("username", username);
		claims.put("email", email);
		
		List<String> roles = new ArrayList<>();
		for (GrantedAuthority authority: authorities) {
			roles.add(authority.getAuthority());
		}
		claims.put("roles", roles);
		
		return claims;
	}
	
	private Claims getClaims(String username, String email, Set<Role> authorities) {
		Claims claims = new DefaultClaims();
		claims.put("username", username);
		claims.put("email", email);
		
		List<String> roles = new ArrayList<>();
		for (Role authority: authorities) {
			roles.add(authority.getName().toString());
		}
		claims.put("roles", roles);
		
		return claims;
	}
	
	private byte[] getDecodedSecret() {
		return Base64.getDecoder().decode("4pE8z3PBoHjnV1AhvGk+e8h2p+ShZpOnpr8cwHmMh1w=");
	}
	
	private Key getKey() {
	    return new SecretKeySpec(getDecodedSecret(), signatureAlgorithm.getJcaName());   
	}
}
