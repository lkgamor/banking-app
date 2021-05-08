package com.louisga.banking.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;


@Entity
@Data
public class Loan implements Serializable {

	private static final long serialVersionUID = 5640938201158286827L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private String id;
	
	@Column(name="loan_id")
	private String loanId;

	@Column(name="loan_date")
	private ZonedDateTime loanDate;

	@Column(name="loan_payed_date")
	private ZonedDateTime loanPayedDate;

	@Column(name="loan_amount")
	private Double loanAmount;
	
	@Column(name="loan_payed")
	private Double loanPayed;

	@Column(name="loan_account_number")
	private String loanAccountNumber;
	
	@Column(name="loan_account_name")
	private String loanAccountName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="loan_branch", referencedColumnName = "branch_id")
	private Branch branch;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name= "issued_by", referencedColumnName = "employee_id")
	private Employee employee;

	@Column(name="loan_status")
	private Boolean loanStatus;
	
}
