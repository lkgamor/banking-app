package com.louisga.banking.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.model.Account;
import com.louisga.banking.model.AccountDTO;
import com.louisga.banking.model.AccountResponse;
import com.louisga.banking.model.AccountToUIDTO;
import com.louisga.banking.model.Branch;
import com.louisga.banking.model.CardType;
import com.louisga.banking.model.EmailPayload;
import com.louisga.banking.model.Transaction;
import com.louisga.banking.repository.AccountRepository;
import com.louisga.banking.repository.TransactionRepository;
import com.louisga.banking.service.AccountService;
import com.louisga.banking.service.BranchService;
import com.louisga.banking.service.CardService;
import com.louisga.banking.utility.AppConstants;
import com.louisga.banking.utility.CentrifugoPublisher;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableAsync
@Transactional
@RequiredArgsConstructor
public class AccountServiceImplementation implements AccountService {

	private final AccountRepository accountRepository;
	private final CardService cardService;
	private final CompanyConfiguration companyConfig;
	private final BranchService branchService;
	private final CentrifugoPublisher centrifugoPublisher;
	private final TransactionRepository transactionRepository;
	private final EmailServiceImplementation emailServiceImpl;
	//private final CustomNewAccountCreatedEventPublisher accountCreatedEventPublisher;
	
	@Override
	public Long getTotalAccounts() throws Exception {
		try {
			log.info("{} ==> requested total number of accounts", AppConstants.ANONYMOUS);
			return accountRepository.count();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of accounts failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch total number of accounts from Database");
		}	
	}
	
	@Override
	public Long getTotalActiveAccounts() throws Exception {
		try {
			log.info("{} ==> requested total number of active accounts", AppConstants.ANONYMOUS);
			return accountRepository.countActiveAccounts();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of active accounts failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch total number of accounts from Database");
		}	
	}
	
	@Override
	public List<Account> getAllAccounts() throws Exception {					
		try {
			log.info("{} ==> requested all accounts", AppConstants.ANONYMOUS);
			return accountRepository.findAll();
		} catch (Exception e) {
			log.error("{}'s ==> request to get all accounts failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch accounts from Database");
		}	
	}
	
	@Override
	public List<Account> getActiveAccounts() throws Exception {		
		try {
			log.info("{} ==> requested all active accounts", AppConstants.ANONYMOUS);
			return accountRepository.findAllActiveAccounts();
		} catch (NoResultException e) {
			log.error("{}'s ==> request to get all active accounts failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch active accounts from Database");
		}	
	}	
	
	@Override
	public Page<Account> getPagedAccounts(PageRequest pageRequest, String daterange) throws Exception {
		try {
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all accounts", AppConstants.ANONYMOUS);
				return accountRepository.findAllAccounts(pageRequest);		
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all accounts created between {} and {}", AppConstants.ANONYMOUS, startDate, endDate);
				return accountRepository.findAllAccountsByDate(pageRequest, startDate, endDate);		
			} 
		} catch (NoResultException e) {
			log.error("{}'s ==> request to get a page of all accounts failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch accounts from Database");
		}			
	}

	@Override
	public Page<Account> getPagedActiveAccounts(PageRequest pageRequest, String daterange) throws Exception {
		try {
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all active accounts", AppConstants.ANONYMOUS);
				return accountRepository.findAllActiveAccounts(pageRequest);	
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all active accounts created between {} and {}", AppConstants.ANONYMOUS, startDate, endDate);
				return accountRepository.findAllActiveAccountsByDate(pageRequest, startDate, endDate);		
			}
		} catch (NoResultException e) {
			log.error("{}'s ==> request to get a page of all active accounts failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch active accounts from Database");
		}			
	}

	@Override
	public Page<Account> getAllAccountsBySearchParams(String accountSearchParam, PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> searching for account matching '{}'", AppConstants.ANONYMOUS, accountSearchParam);
			return accountRepository.findAccountsMatching(accountSearchParam, pageRequest);
		} catch (Exception e) {
			log.info("{}'s ==> search for account matching '{}' failed", AppConstants.ANONYMOUS, accountSearchParam);
			throw new Exception("Could not fetch account records matching ["+ accountSearchParam + "]");	
		}
	}
	
	@Override
	public AccountToUIDTO getAccountDetails(String accountNumber) throws Exception {		
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber.trim());
		if(account.isPresent()) {
			try {
				String accountName = account.get().getAccountName();
				log.info("{} ==> requested details of account '{}'", AppConstants.ANONYMOUS, accountName);

			    ArrayList<Transaction> accountTransactions = transactionRepository.findAllTransactionsForAccount(accountNumber);
				AccountToUIDTO accountToUIDTO = AccountDTO.toDTOFromEntity(account.get(), accountTransactions);
				return accountToUIDTO;
			} catch (Exception e) {
				log.info("{}'s ==> request to get details of account with number '{}' failed", AppConstants.ANONYMOUS, accountNumber);
				throw new Exception("Could not fetch account with number [ " + accountNumber + "] from Database");
			}
		} else {
			throw new NotFoundException("Account with number ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Override
	public Account saveAccount(AccountDTO accountDTO) throws Exception {
	    CardType card = cardService.getCardDetails(accountDTO.getAccountIDType());
	    
	    String accountName = accountDTO.getAccountName();

		Branch branch = branchService.getBranchDetails("21cfe333-042c-43ad-90bd-851437cdaea0");
		
	    Account accountToSave = AccountDTO.toEntityFromDTO(companyConfig.getAccountNamesPrefix(), accountDTO, card, branch);
	    
		try {
			log.info("{} ==> created account '{}'", AppConstants.ANONYMOUS, accountName);
			Account accountToReturn = accountRepository.save(accountToSave);
			
			try {
				AccountResponse response = AccountDTO.toCentrifugoResponseFromEntity(accountToReturn);
				centrifugoPublisher.sendAccountCreatedToCentrifugo(response);
				//accountCreatedEventPublisher.publishCreatedAccountEvent(accountName, accountToReturn.getAccountNumber(), employeeFullName, accountToReturn.getAccountDateOpened());
				log.info("Sent created account '{}' details to Centrifugo", accountName);
			} catch (Exception e) {
				log.info("Failed to send account '{}' details to Centrifugo", accountName);
			}	
			
			//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
			if (companyConfig.getSendEmails()) {
				String accountEmail = accountToSave.getAccountEmail();
				
				//CHECK IF ACCOUNT HAS AN EMAIL ADDRESS	
				if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
					String emailSubject = "ACCOUNT OPENED";
					String emailMessage = "Hello <em>" + accountToSave.getAccountName() + "</em>! Your account with <u>" 
								+ companyConfig.getName()
								+ "</u> has been successfully opened. Your account number is <strong>" + accountToSave.getAccountNumber() 
								+ "</strong> and your initial account balance is <strong>GH₵ " + companyConfig.getInitialAccountBalance()
								+ "</strong>. We look forward to a profitable relationship with you.";
					buildEmail(accountEmail, emailSubject, emailMessage);
				}
			}
			
			return accountToReturn;
		} catch (Exception e) {
			log.info("{}'s ==> request to create account '{}' failed", AppConstants.ANONYMOUS, accountName);
			throw new Exception("Save Account [" + accountDTO.getAccountName() + "] Failed");			
		}		
	}

	@Override
	public Double getAccountBalance(String accountNumber) throws Exception {				
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			try {
				log.info("{} ==> requested account balance of account '{}'", AppConstants.ANONYMOUS, accountName);
				return accountRepository.getAccountBalance(accountNumber);
			} catch (Exception e) {
				log.info("{}'s ==> request to get account balance of account '{}' failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Get Balance for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Override
	public Boolean getLoanStatus(String accountNumber) throws Exception {			
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			try {
				log.info("{} ==> requested loan status of account '{}'", AppConstants.ANONYMOUS, accountName);
				return accountRepository.getAccountLoanStatus(accountNumber);
			} catch (Exception e) {
				log.info("{}'s ==> request to get loan status of account '{}' failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Get Loan status for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Override
	public void updateAccountBalance(String accountNumber, Double newAccountBalance) throws Exception {		
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			
			try {
				log.info("{} ==> updated account balance of account '{}'", AppConstants.ANONYMOUS, accountName);
				accountRepository.setNewAccountBalance(accountNumber, newAccountBalance);
				
				//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
				if (companyConfig.getSendEmails()) {
					String accountEmail = account.get().getAccountEmail();
					
					//CHECK IF ACCOUNT HAS AN EMAIL ADDRESS	
					if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
						String emailSubject = "ACCOUNT BALANCE UPDATED";
						String emailMessage = "Hello <em>" + accountName + "</em>! Please be informed that your account balance for <strong>"
								+  accountNumber + "</strong> has been updated. " 
								+ "Total Account Balance is now <strong>GH₵ " + newAccountBalance + "</strong>";
						buildEmail(accountEmail, emailSubject, emailMessage);
					}
				}
				
			} catch (Exception e) {
				log.error("{}'s ==> request to update account balance of account '{}' in database failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Update Balance for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}

	@Override
	public void updateAccountLoanStatus(String loanAccountNumber, boolean b) throws Exception {
		Optional<Account> account = accountRepository.findByAccountNumber(loanAccountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			try {
				log.info("{} ==> updated loan status of account '{}'", AppConstants.ANONYMOUS, accountName);
				accountRepository.updateLoanStatus(loanAccountNumber, b);		
			} catch (Exception e) {
				log.error("{}'s ==> request to update loan status of account '{}' in database failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Update Loan Status for Account [" + loanAccountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ loanAccountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}

	@Override
	public Account updateAccount(String accountNumber, AccountDTO newAccount) throws Exception {			
		Optional<Account> oldAccount = accountRepository.findByAccountNumber(accountNumber);
		if(oldAccount.isPresent()) {
			String accountName = oldAccount.get().getAccountName();
			try {
				oldAccount.get().setAccountContact(newAccount.getAccountContact());
				oldAccount.get().setAccountEmail(newAccount.getAccountEmail());
				oldAccount.get().setAccountOccupation(newAccount.getAccountOccupation());
				oldAccount.get().setAccountKinContact(newAccount.getAccountKinContact());
				oldAccount.get().setAccountKinName(newAccount.getAccountKinName());
				
				if(newAccount.getAccountImage() != null) 
					oldAccount.get().setAccountImage(newAccount.getAccountImage());
				if(newAccount.getAccountSignature() != null) 
					oldAccount.get().setAccountSignature(newAccount.getAccountSignature());
				
				Account updatedAccountDetails =  accountRepository.save(oldAccount.get());
				log.info("{} ==> updated details of account '{}'", AppConstants.ANONYMOUS, accountName);
				
				if(newAccount.getAccountSignature() != null) {

					//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
					if (companyConfig.getSendEmails()) {
						String accountEmail = newAccount.getAccountEmail();
						
						//CHECK IF ACCOUNT HAS AN EMAIL ADDRESS	
						if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
							String emailSubject = "ACCOUNT SIGNATURE UPDATED";
							String emailMessage = "Hello <em>" + accountName + "</em>! The signature for your account <strong>"
									+  accountNumber + "</strong> has been updated.";
							buildEmail(accountEmail, emailSubject, emailMessage);
						}
					}
				}
				
				return updatedAccountDetails;
				
			} catch (Exception e) {
				log.error("{}'s ==> request to update details of account '{}' in database failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Update Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}
	}
	
	@Override
	public void updateAccountImage(String accountNumber, byte[] newAccountImage) throws Exception {			
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			
			try {
				accountRepository.updateAccountImage(accountNumber, newAccountImage);
				log.info("{} ==> updated picture of account '{}'", AppConstants.ANONYMOUS, accountName);
				
				//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS
				if (companyConfig.getSendEmails()) {
					String accountEmail = account.get().getAccountEmail();
					
					//CHECK IF ACCOUNT HAS AN EMAIL ADDRESS
					if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
						String emailSubject = "ACCOUNT PROFILE PICTURE UPDATED";
						String emailMessage = "Hello <em>" + accountName + "</em>! The profile picture for your account <strong>"
								+  accountNumber + "</strong> has been updated.";
						buildEmail(accountEmail, emailSubject, emailMessage);
					}
				}
				
			} catch (Exception e) {
				log.error("{}'s ==> request to update picture of account '{}' in database failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Update Picture for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}
	}
	
	@Override
	public void updateAccountSignature(String accountNumber, byte[] newAccountSignature) throws Exception {			
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			
			try {
				accountRepository.updateAccountSignature(accountNumber, newAccountSignature);
				log.info("{} ==> updated signature of account '{}'", AppConstants.ANONYMOUS, accountName);
				
				//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
				if (companyConfig.getSendEmails()) {
					String accountEmail = account.get().getAccountEmail();
					
					//CHECK IF ACCOUNT HAS AN EMAIL ADDRESS	
					if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
						String emailSubject = "ACCOUNT SIGNATURE UPDATED";
						String emailMessage = "Hello <em>" + accountName + "</em>! The signature for your account <strong>"
								+  accountNumber + "</strong> has been updated.";
						buildEmail(accountEmail, emailSubject, emailMessage);
					}
				}
				
			} catch (Exception e) {
				log.error("{}'s ==> request to update signature of account '{}' in database failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Update Image for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}
	}
	
	@Override
	public String deleteAccount(String accountNumber) throws Exception {
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			
			if(!account.get().getAccountOwing()) {
				
				if(account.get().getAccountBalance() <= companyConfig.getMinimumAllowedAccountBalance()) {				
					try {
						accountRepository.closeAccount(accountNumber, ZonedDateTime.now(), false);
						log.info("{} ==> closed account '{}'", AppConstants.ANONYMOUS, accountName);
						
						//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
						if (companyConfig.getSendEmails()) {
							String accountEmail = account.get().getAccountEmail();
							
							//CHECK IF ACCOUNT HAS AN EMAIL ADDRESS	
							if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
								String emailSubject = "ACCOUNT CLOSED";
								String emailMessage = "Hello <em>" + accountName + "</em>! Please be informed that your account <strong>"
										+  accountNumber + "</strong> with <u>" + companyConfig.getName() 
										+ "</u> has been successfully closed. "
										+ "It has been a pleasure working with you.";
								buildEmail(accountEmail, emailSubject, emailMessage);
							}
						}
						
						return accountNumber;
					} catch (Exception e) {
						log.info("{}'s ==> request to close account '{}' failed", AppConstants.ANONYMOUS, accountName);
						throw new Exception("Delete Account [" + accountNumber + "] Failed");
					}
				} else {
					String genderRef = account.get().getAccountUserGender().equalsIgnoreCase("Male") ? "his" : "her";
					throw new Exception(accountName + " still has some funds in " + genderRef + " account. Please ensure complete withdrawal before account can be closed!");
				}
				
			} else {
				String genderRef = account.get().getAccountUserGender().equalsIgnoreCase("Male") ? "he" : "she";
				throw new Exception(accountName + " still owes some unpayed loans. Please ensure " + genderRef + " completes all payments before account can be closed!");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}		
	}

	@Override
	public String getAccountImageForWeb(String accountNumber) throws Exception {
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			try {
				log.info("{} ==> requested Image of account '{}'", AppConstants.ANONYMOUS, accountName);
				if (accountRepository.getAccountImage(accountNumber) != null) 
					return Base64.getEncoder().encodeToString(accountRepository.getAccountImage(accountNumber));
				else
					return null;
			} catch (Exception e) {
				log.info("{}'s ==> request to get Image of account '{}' failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Get Image for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}

	@Override
	public byte[] getAccountImageForMobile(String accountNumber) throws Exception {
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			try {
				log.info("{} ==> requested Image of account '{}'", AppConstants.ANONYMOUS, accountName);
				if (accountRepository.getAccountImage(accountNumber) != null) 
					return accountRepository.getAccountImage(accountNumber);
				else
					return null;
			} catch (Exception e) {
				log.info("{}'s ==> request to get Image of account '{}' failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Get Image for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}

	@Override
	public Boolean getAccountStatus(String accountNumber) throws Exception {
		Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
		if(account.isPresent()) {
			String accountName = account.get().getAccountName();
			try {
				log.info("{} ==> requested status of account '{}'", AppConstants.ANONYMOUS, accountName);
				return accountRepository.getAccountStatus(accountNumber);
			} catch (Exception e) {
				log.info("{}'s ==> request to get status of account '{}' failed", AppConstants.ANONYMOUS, accountName);
				throw new Exception("Get status for Account [" + accountNumber + "] Failed");
			}
		} else {
			throw new NotFoundException("Account with id ["+ accountNumber +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Async
	private void buildEmail( String emailAddress, String emailSubject, String emailMessage) throws UnsupportedEncodingException, MessagingException {
		EmailPayload payload = new EmailPayload();
		payload.setTo(emailAddress);
		payload.setSubject(emailSubject);
		payload.setBody(emailMessage);
		payload.setDateTime(ZonedDateTime.now());
		emailServiceImpl.processEmail(payload);		
	}

	@Override
	public String getAccountEmail(String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested the email address for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return accountRepository.findAccountEmailAddress(accountNumber);
		} catch (Exception e) {
			log.info("{}'s ==> request to get the email address for account '{}' failed", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch email address for account ["+ accountNumber + "]");	
		}
	}
}
