package com.louisga.banking.model;


import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class TransactionResponse {

	private String transactionId;

	private Double accountBalance;

	private String transactionType;
	
	private String transactionBranch;

	private Double transactionAmount;

	private ZonedDateTime transactionDate;
	
	private String transactionAccountName;
	
	private String transactionAccountNumber;
}
