package com.louisga.banking.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class Employee implements Serializable  {

	private static final long serialVersionUID = -2675787519590790253L;
	
	@Id
	@Column(name="employee_id")
	private String employeeId;

	@Column(name="employee_first_name")
	private String employeeFirstName;

	@Column(name="employee_last_name")
	private String employeeLastName;

	@Column(name="employee_contact")
	private String employeeContact;

	@Column(name="employee_dob")
	private String employeeDOB;

	@Column(name="employee_gender")
	private String employeeGender;

	@Column(name="employee_address")
	private String employeeAddress;
	
	@Column(name="employee_email")
	private String employeeEmail;
	
	@Column(name="working_status")
	private Boolean employeeWorkingStatus;
	
	@Column(name="date_created")
	private ZonedDateTime dateCreated;
	
	@Column(name="date_terminated")
	private ZonedDateTime dateTerminated;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "employee_role", referencedColumnName = "role_id")
	private Role role;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "employee_branch", referencedColumnName = "branch_id")
	private Branch branch;	
}
