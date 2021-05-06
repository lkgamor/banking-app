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
public class Withdrawal implements Serializable {

	private static final long serialVersionUID = 7347989016950345006L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private String id;
	
	@Column(name="withdrawal_id")
	private String withdrawalId;

	@Column(name="withdrawal_date")
	private ZonedDateTime withdrawalDate;

	@Column(name="withdrawal_amount")
	private Double withdrawalAmount;

	@Column(name="total_balance")
	private Double totalBalance;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="withdrawal_branch", referencedColumnName = "branch_id")
	private Branch branch;

	@Column(name="withdrawal_account_number")
	private String withdrawalAccountNumber;

	@Column(name="withdrawal_account_name")
	private String withdrawalAccountName;

}
