package com.abnamro.nl.favouriterecipe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("com.abnamro.nl.favouriterecipe.controller"))              
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(apiInfo());                                           
    }
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Favourite Recipe")
				.description("CRUD Application with Search and Filter feature for Recipes")
				.termsOfServiceUrl("https://github.com/rakinulhuq/favourite-recipe")
				.contact(new Contact("Rakinul Huq", "https://github.com/rakinulhuq/favourite-recipe","rakinul.xxx@gmail.com"))
				.license("Favourite Recipe License")
				.licenseUrl("https://github.com/rakinulhuq/favourite-recipe")
				.version("2.0")
				.build();
	}
}
