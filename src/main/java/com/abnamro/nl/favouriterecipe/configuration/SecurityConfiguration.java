package com.abnamro.nl.favouriterecipe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
	
	private static final String[] AUTH_WHITELIST = {
	        "/swagger-resources/**",
	        "/swagger-ui/**",
	        "/v2/api-docs",
	        "/webjars/**"
	};
	
	private AuthTokenFilter authTokenFilter;
	
	@Autowired
	public SecurityConfiguration(AuthTokenFilter authTokenFilter) {
		this.authTokenFilter = authTokenFilter;
	}
	
	@Bean
	public AuditorAware<String> auditorAware(){
	    return new SpringSecurityAuditorAware();
	}
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return authTokenFilter;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		return authenticationManagerBuilder.build();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthEntryPointJwt entryPoint) throws Exception {	
		http
		.cors().and()
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/management/**").permitAll()
		.antMatchers("/auth/**").permitAll()
		.antMatchers(AUTH_WHITELIST).permitAll()
		.anyRequest().authenticated().and()
		.authenticationManager(authenticationManagerBean(http))
		.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
		.exceptionHandling().authenticationEntryPoint(entryPoint).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
        return http.build();
	}
}
