package com.louisga.banking.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Role {

	@Id
	@Column(name="role_id")
	private String roleId;
	
	@Column(name="role_name")
	private String roleName;
	
}
