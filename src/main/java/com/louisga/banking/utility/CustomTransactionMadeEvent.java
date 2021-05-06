package com.louisga.banking.utility;

import java.time.ZonedDateTime;

import lombok.Data;

/**
 * @author LouisGa
 */

@Data
public class CustomTransactionMadeEvent {

	private String transactionType;
	private Double transactionAmount;
	private String transactionIssuedBy;
	private ZonedDateTime transactionDate;	
	private String transactionAccountNumber;
	
	public CustomTransactionMadeEvent(String transactionType, Double transactionAmount, String transactionAccountNumber,
			ZonedDateTime transactionDate, String transactionIssuedBy) {
		this.transactionType = transactionType;
		this.transactionAmount = transactionAmount; 
		this.transactionIssuedBy = transactionIssuedBy; 
		this.transactionDate = transactionDate; 
		this.transactionAccountNumber = transactionAccountNumber; 
 	}
}
