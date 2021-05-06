package com.louisga.banking.model;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class AccountResponse {

	private String accountName;
	private String accountNumber;
	private String openedBy;
	private ZonedDateTime dateOpened;
}
