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
public class Deposit implements Serializable {

	private static final long serialVersionUID = -5907289430890037146L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private String id;
	
	@Column(name= "deposit_id")
	private String depositId;
	
	@Column(name= "deposit_date")
	private ZonedDateTime depositDate;
	
	@Column(name= "deposit_amount")
	private Double depositAmount;
	
	@Column(name= "total_balance")
	private Double totalBalance;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="deposit_branch", referencedColumnName = "branch_id")
	private Branch branch;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name= "issued_by", referencedColumnName = "employee_id")
	private Employee employee;
	
	@Column(name= "deposited_account_number")
	private String depositedAccountNumber;

	@Column(name= "deposited_account_name")
	private String depositedAccountName;

}
