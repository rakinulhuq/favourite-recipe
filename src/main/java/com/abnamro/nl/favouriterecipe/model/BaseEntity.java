package com.abnamro.nl.favouriterecipe.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {

	@Id
	private String id = UUID.randomUUID().toString();
	
	@Version      
    private Long version;
    
	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String updatedBy;
	
	@CreatedDate()
	private LocalDateTime creationDate;
	
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

//	public BaseEntity() {
//		initialize(UUID.randomUUID().toString());
//	}
//	
//	public BaseEntity(String id) {
//		initialize(id);
//	}
//	
//	public void initialize(String id) {
//		this.id = id;
//		this.createdBy = this.id;
//	}
}
