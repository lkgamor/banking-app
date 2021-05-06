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
public class Transaction implements Serializable {

	private static final long serialVersionUID = 394180998823655031L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="transaction_id")
	private String transactionId;

	@Column(name="transaction_type")
	private String transactionType;

	@Column(name="transaction_date")
	private ZonedDateTime transactionDate;

	@Column(name="transaction_amount")
	private Double transactionAmount;

	@Column(name="total_balance")
	private Double totalBalance;
	
	@Column(name="transaction_account_number")
	private String transactionAccountNumber;
	
	@Column(name="transaction_account_name")
	private String transactionAccountName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="transaction_branch", referencedColumnName = "branch_id")
	private Branch branch;
}