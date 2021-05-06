package com.louisga.banking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.Account;
import com.louisga.banking.model.AccountDTO;
import com.louisga.banking.model.AccountToUIDTO;

public interface AccountService {

	Long getTotalAccounts() throws Exception;

	Long getTotalActiveAccounts() throws Exception;

	List<Account> getAllAccounts() throws Exception;

	List<Account> getActiveAccounts() throws Exception;

	Page<Account> getPagedAccounts(PageRequest pageRequest, String daterange) throws Exception;

	Page<Account> getPagedActiveAccounts(PageRequest pageRequest, String daterange) throws Exception;

	Page<Account> getAllAccountsBySearchParams(String accountSearchParam, PageRequest pageRequest) throws Exception;

	AccountToUIDTO getAccountDetails(String accountNumber) throws Exception;

	Account saveAccount(AccountDTO accountDTO) throws Exception;

	Double getAccountBalance(String accountNumber) throws Exception;

	String getAccountEmail(String accountNumber) throws Exception;

	String getAccountImageForWeb(String accountNumber) throws Exception;

	byte[] getAccountImageForMobile(String accountNumber) throws Exception;

	Boolean getAccountStatus(String accountNumber) throws Exception;

	Boolean getLoanStatus(String accountNumber) throws Exception;

	void updateAccountBalance(String accountNumber, Double newAccountBalance) throws Exception;

	void updateAccountLoanStatus(String loanAccountNumber, boolean b) throws Exception;

	Account updateAccount(String accountNumber, AccountDTO newAccount) throws Exception;

	void updateAccountImage(String accountNumber, byte[] newAccountImage) throws Exception;

	void updateAccountSignature(String accountNumber, byte[] newAccountSignature) throws Exception;

	String deleteAccount(String accountNumber) throws Exception;

}