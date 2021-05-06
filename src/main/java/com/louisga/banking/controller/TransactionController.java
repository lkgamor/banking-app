package com.louisga.banking.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.louisga.banking.model.Deposit;
import com.louisga.banking.model.Loan;
import com.louisga.banking.model.LoanPayment;
import com.louisga.banking.model.Transaction;
import com.louisga.banking.model.Withdrawal;
import com.louisga.banking.service.AccountService;
import com.louisga.banking.service.TransactionService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TransactionController {
	
	private final AccountService accountService;
	private final TransactionService transactionService;

	private List<String> genderList = List.of("Male", "Female");

	/**
	 * 
	 * @param model
	 * @param assembler
	 * @param user
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/transactions")
	String getAllTransactions(Model model, 
							  PagedResourcesAssembler<Transaction> assembler, 
							  @RequestParam(defaultValue = "0") int page, 
							  @RequestParam(defaultValue = "transactionAccountName") String sort,
							  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
							  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception {
		
		Long totalTransactions;
		Page<Transaction> transactionList = transactionService.getPagedTransactions(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		totalTransactions = transactionList.getTotalElements();
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTIONS, assembler.toModel(transactionList));

		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, totalTransactions);
		return "pages/transactions/index";
	}
	
	/**
	 * 
	 * @param model
	 * @param user
	 * @param page
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/transactions/add")
	String getAddTransactionPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "transactionAccountName") String sort) throws Exception {
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_TITLE, "New Transaction");
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTION_ACCOUNT,"<i class='fas fa-coins'></i> New Transaction");
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNTS, accountService.getActiveAccounts());	
		return "pages/transactions/form";
	}
	
	/**
	 * 
	 * @param id
	 * @param model
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@GetMapping({"/transactions/{id}","/deposits/{id}","/withdrawals/{id}"})
	String getTransactionDetailsPage(@PathVariable String id, Model model) throws Exception {
		Transaction transaction = transactionService.getTransactionDetails(id);
		String accountNumber = transaction.getTransactionAccountNumber();
		
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_GENDERS, genderList);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTION, transaction);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_TITLE, transaction.getTransactionId());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_STATUS, accountService.getAccountStatus(accountNumber));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_LOAN_STATUS, accountService.getLoanStatus(accountNumber));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_IMAGE, accountService.getAccountImageForWeb(accountNumber));
		return "pages/transactions/details";
	}
	
	/**
	 * 
	 * @param model
	 * @param assembler
	 * @param user
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/loans")
	String getAllLoans(Model model, 
					   PagedResourcesAssembler<Loan> assembler, 
					   @RequestParam(defaultValue = "0") int page, 
					   @RequestParam(defaultValue = "loanAccountName") String sort,
					   @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
					   @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception {
		
		Page<Loan> loanList = transactionService.getPagedLoans(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_LOANS, assembler.toModel(loanList));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, loanList.getTotalElements());
		return "pages/transactions/loans/index";
	}
	
	/**
	 * 
	 * @param loanId
	 * @param model
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/loans/{loanId}")
	String getLoanDetailsPage(@PathVariable String loanId, Model model) throws Exception {
		
		Transaction transaction = transactionService.getTransactionDetails(loanId);
		ArrayList<LoanPayment> loanPaymentRecords = transactionService.getLoanPaymentsForLoan(loanId);
		
		String accountNumber = transaction.getTransactionAccountNumber();
		String accountImage = accountService.getAccountImageForWeb(accountNumber);
		
		Boolean accountStatus = accountService.getAccountStatus(accountNumber);
		Boolean accountLoanStatus = accountService.getLoanStatus(accountNumber);
		
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTION, transaction);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_TITLE, transaction.getTransactionId());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_STATUS, accountStatus);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_LOAN_STATUS, accountLoanStatus);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_LOAN_PAYMENT_RECORDS, loanPaymentRecords);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_IMAGE, accountImage);
		return "pages/transactions/loans/details";
	}
	
	/**
	 * 
	 * @param model
	 * @param assembler
	 * @param user
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/withdrawals")
	String getAllWithdrawals(Model model, 
							 PagedResourcesAssembler<Withdrawal> assembler, 
							 @RequestParam(defaultValue = "0") int page, 
							 @RequestParam(defaultValue = "withdrawalAccountName") String sort,
							 @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
							 @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception {

		Page<Withdrawal> withdrawalList = transactionService.getPagedWithdrawals(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		Long totalTransactions = withdrawalList.getTotalElements();
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_WITHDRAWALS, assembler.toModel(withdrawalList));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, totalTransactions);
		return "pages/transactions/withdrawals/index";
	}
	
	/**
	 * 
	 * @param model
	 * @param assembler
	 * @param user
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/deposits")
	String getAllDeposits(Model model, 
						  PagedResourcesAssembler<Deposit> assembler, 
						  @RequestParam(defaultValue = "0") int page, 
						  @RequestParam(defaultValue = "depositedAccountName") String sort,
						  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
						  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception {

		Page<Deposit> depositList = transactionService.getPagedDeposits(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		Long totalTransactions = depositList.getTotalElements();
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_DEPOSITS, assembler.toModel(depositList));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, totalTransactions);
		return "pages/transactions/deposits/index";
	}
}
