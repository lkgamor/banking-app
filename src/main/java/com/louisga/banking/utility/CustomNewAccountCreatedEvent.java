package com.louisga.banking.utility;

import java.time.ZonedDateTime;

import lombok.Data;

/**
 * @author LouisGa
 */

@Data
public class CustomNewAccountCreatedEvent {

	private String accountName;
	private String accountNumber;
	private String openedBy;
	private ZonedDateTime dateOpened;	
	
	public CustomNewAccountCreatedEvent(String accountName, String accountNumber, String openedBy, ZonedDateTime dateOpened) {
		this.accountName = accountName;
		this.accountNumber = accountNumber; 
		this.openedBy = openedBy; 
		this.dateOpened = dateOpened; 
 	}
}
