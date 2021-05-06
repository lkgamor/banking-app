package com.louisga.banking.model;


import lombok.Data;

@Data
public class TransactionDTO {

	private String id;
	
	private String transactionId;

	private Double totalBalance;

	private String transactionDate;

	private String transactionType;
	
	private String transactionBranch;

	private Double transactionAmount;
	
	private String transactionAccountName;
	
	private String transactionAccountNumber;
}
