package com.louisga.banking.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
	
	private String accountName;
	
	private String accountContact;
	
	private String accountKinName;
	
	private String accountKinContact;
	
	private String accountEmail;
		
	private String accountUserDOB;

	private Double accountBalance;
	
	private String accountIDType;

	private String accountIDNumber;

	private String accountOccupation;
	
	private String accountUserGender;
	
	private ZonedDateTime accountDateOpened;
	
	private ZonedDateTime accountDateClosed;
	
	private String accountSignature64;
	
	private String accountImage64;
	
	private byte[] accountSignature;
	
	private byte[] accountImage;
	
	public static Account toEntityFromDTO(String prefix, AccountDTO accountDTO, CardType card, Branch branch) {
		Random rand = new Random();
	    String accountNumber = prefix;
	    for (int i = 0; i < 5; i++) {
	        int n = rand.nextInt(10) + 0;
	        accountNumber += Integer.toString(n);
	    }
		
		Account account = new Account();
	    account.setCard(card);
	    account.setAccountOwing(false);
	    account.setAccountStatus(true);
	    account.setAccountBalance(0.00);
	    account.setBranch(branch);
	    account.setAccountNumber(accountNumber);
	    account.setAccountDateOpened(ZonedDateTime.now());
	    account.setAccountName(accountDTO.getAccountName());
	    account.setAccountKinName(accountDTO.getAccountKinName());
	    account.setAccountKinContact(accountDTO.getAccountKinContact());
	    account.setAccountEmail(accountDTO.getAccountEmail());
	    account.setAccountContact(accountDTO.getAccountContact());
	    account.setAccountUserDOB(accountDTO.getAccountUserDOB());
	    account.setAccountIDNumber(accountDTO.getAccountIDNumber());	    
	    account.setAccountOccupation(accountDTO.getAccountOccupation());
	    account.setAccountUserGender(accountDTO.getAccountUserGender());
	    account.setAccountSignature(accountDTO.getAccountSignature());
	    account.setAccountImage(accountDTO.getAccountImage());
	    return account;
	}
	
	public static AccountToUIDTO toDTOFromEntity(Account account, ArrayList<Transaction> accountTransactions) {
		AccountToUIDTO accountDTO = new AccountToUIDTO();
		accountDTO.setAccountName(account.getAccountName());
		accountDTO.setAccountEmail(account.getAccountEmail());
		accountDTO.setAccountOwing(account.getAccountOwing());
		accountDTO.setAccountStatus(account.getAccountStatus());
		accountDTO.setAccountNumber(account.getAccountNumber());
		accountDTO.setAccountContact(account.getAccountContact());
		accountDTO.setAccountUserDOB(account.getAccountUserDOB());
		accountDTO.setAccountBalance(account.getAccountBalance());
		accountDTO.setAccountIDNumber(account.getAccountIDNumber());
		accountDTO.setAccountKinName(account.getAccountKinName());
		accountDTO.setAccountKinContact(account.getAccountKinContact());
		accountDTO.setAccountDateOpened(account.getAccountDateOpened());
		accountDTO.setAccountDateClosed(account.getAccountDateClosed());
		accountDTO.setAccountOccupation(account.getAccountOccupation());
		accountDTO.setAccountUserGender(account.getAccountUserGender());
		
		if(account.getAccountSignature() == null) 
			accountDTO.setAccountSignature64("");
		else
			accountDTO.setAccountSignature64(Base64.getEncoder().encodeToString(account.getAccountSignature()));
		
		if(account.getAccountImage() == null) 
			accountDTO.setAccountImage64("");
		else
			accountDTO.setAccountImage64(Base64.getEncoder().encodeToString(account.getAccountImage()));
		
		accountDTO.setAccountSignature(account.getAccountSignature());
		accountDTO.setAccountImage(account.getAccountImage());
		accountDTO.setCard(account.getCard());
		accountDTO.setBranch(account.getBranch());
		accountDTO.setTransactions(accountTransactions);
		return accountDTO;		
	}	
	
	public static AccountResponse toCentrifugoResponseFromEntity(Account account) {
		AccountResponse centrifugoResponse = new AccountResponse();
		centrifugoResponse.setAccountName(account.getAccountName());
		centrifugoResponse.setAccountNumber(account.getAccountNumber());
		centrifugoResponse.setDateOpened(account.getAccountDateOpened());
		return centrifugoResponse;		
	}
}
