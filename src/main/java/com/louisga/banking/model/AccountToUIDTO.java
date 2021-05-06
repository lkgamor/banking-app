package com.louisga.banking.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import lombok.Data;

@Data
public class AccountToUIDTO {
	
	private String accountName;
	
	private String accountNumber;
	
	private String accountContact;
	
	private String accountKinName;
	
	private String accountKinContact;
	
	private String accountEmail;
		
	private String accountUserDOB;

	private Double accountBalance;
	
	private Boolean accountOwing;
	
	private Boolean accountStatus;
	
	private String accountIDNumber;

	private String accountOccupation;
	
	private String accountUserGender;
	
	private ZonedDateTime accountDateOpened;
	
	private ZonedDateTime accountDateClosed;
	
	private String accountSignature64;
	
	private String accountImage64;
	
	private byte[] accountSignature;
	
	private byte[] accountImage;
	
	private CardType card;

	private Branch branch;
	
	private ArrayList<Transaction> transactions;
	
}
