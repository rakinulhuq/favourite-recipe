package com.abnamro.nl.favouriterecipe.configuration;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abnamro.nl.favouriterecipe.model.auth.Role;
import com.abnamro.nl.favouriterecipe.model.auth.UserRole;
import com.abnamro.nl.favouriterecipe.repository.RoleRepository;

@Component
public class DatabaseInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
	
	private RoleRepository roleRepository;
	
	@Autowired
	public DatabaseInitializer(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}
	
	@PostConstruct
    public void init() {
		LOGGER.info("Database Initialization Started ...");
		if (roleRepository.findByName(UserRole.ROLE_USER).isEmpty()) {
			roleRepository.save(new Role("c7250c9e-9393-4155-8a4a-a8eadbe9943f", UserRole.ROLE_USER));
		}
		if (roleRepository.findByName(UserRole.ROLE_ADMIN).isEmpty()) {
			roleRepository.save(new Role("f8c13d3e-19fe-440c-bfe8-a0837a5eca59", UserRole.ROLE_ADMIN));
		}
		LOGGER.info("Database Initialization Ended...");
    }
}
