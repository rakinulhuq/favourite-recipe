package com.abnamro.nl.favouriterecipe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/management")
public class ManagementController {

	@Autowired
	public ManagementController() {
		
	}
	
	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("Service is up!");
	}
}
