package com.louisga.banking.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Branch implements Serializable {

	private static final long serialVersionUID = -4159269273710021471L;
	
	@Id
	@Column(name= "branch_id")
	private String branchId;
	
	@Column(name= "branch_name")
	private String branchName;

	@Column(name= "branch_address")
	private String branchAddress;
	
	@Column(name= "branch_state")
	private String branchState;
	
}
