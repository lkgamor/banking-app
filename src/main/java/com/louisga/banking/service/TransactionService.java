package com.louisga.banking.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.Deposit;
import com.louisga.banking.model.Loan;
import com.louisga.banking.model.LoanPayment;
import com.louisga.banking.model.LoanToPay;
import com.louisga.banking.model.Transaction;
import com.louisga.banking.model.TransactionDTO;
import com.louisga.banking.model.TransactionResponse;
import com.louisga.banking.model.Withdrawal;

import javassist.NotFoundException;

public interface TransactionService {

	/**
	 * LOANS BUSINESS LOGIC	 
	 */
	Long getTotalLoans() throws Exception;

	Loan getLoanForAccount(String accountNumber) throws NotFoundException;

	void saveLoan(Loan loan) throws Exception;

	void confirmLoanPayed(String loanId, Double amountPayed, String accountNumber, ZonedDateTime loanPaymentDate);

	Double makeLoanPayment(String loanId, LoanToPay loan) throws Exception;

	ArrayList<Loan> getLoans() throws Exception;

	ArrayList<Loan> getLoansForAccount(String accountNumber) throws Exception;
	
	ArrayList<LoanPayment> getLoanPaymentsForLoan(String loanId) throws Exception;

	Page<Loan> getPagedLoansForAccount(PageRequest pageRequest, String accountNumber) throws Exception;

	Page<Loan> getPagedLoans(PageRequest pageRequest, String employeeToFilterBy) throws Exception;

	Page<Loan> getAllLoansBySearchParams(String transactionSearchParam, PageRequest pageRequest) throws Exception;

	/**
	 * DEPOSITS BUSINESS LOGIC	 
	 */
	Long getTotalDeposits() throws Exception;

	ArrayList<Deposit> getDeposits() throws java.lang.Exception;

	ArrayList<Deposit> getDepositsForAccount(String accountNumber) throws Exception;

	Page<Deposit> getPagedDepositsForAccount(PageRequest pageRequest, String accountNumber) throws Exception;

	Page<Deposit> getPagedDeposits(PageRequest pageRequest, String employeeToFilterBy) throws Exception;

	Page<Deposit> getAllDepositsBySearchParams(String transactionSearchParam, PageRequest pageRequest) throws Exception;

	/**
	 * WITHDRAWALS BUSINESS LOGIC	 
	 */
	Long getTotalWithdrawals() throws Exception;

	ArrayList<Withdrawal> getWithdrawals() throws java.lang.Exception;

	ArrayList<Withdrawal> getWithdrawalsForAccount(String accountNumber) throws Exception;

	Page<Withdrawal> getPagedWithdrawalsForAccount(PageRequest pageRequest, String accountNumber) throws Exception;

	Page<Withdrawal> getPagedWithdrawals(PageRequest pageRequest, String employeeToFilterBy) throws java.lang.Exception;

	Page<Withdrawal> getAllWithdrawalsBySearchParams(String transactionSearchParam, PageRequest pageRequest) throws Exception;

	/**
	 * GENERAL TRANSACTIONS BUSINESS LOGIC	 
	 */
	Long getTotalTransactions() throws Exception;

	ArrayList<Transaction> getAllTransactions() throws Exception;

	Transaction getTransactionDetails(String id) throws Exception;

	TransactionResponse saveTransaction(TransactionDTO transactionDTO) throws Exception;

	Page<Transaction> getPagedTransactionsForAccount(PageRequest pageRequest, String accountNumber) throws Exception;

	Page<Transaction> getRecentTransactions(PageRequest pageRequest) throws Exception;

	Page<Transaction> getPagedTransactions(PageRequest pageRequest, String employeeToFilterBy) throws Exception;

	Page<Transaction> getAllTransactionsBySearchParams(String transactionSearchParam, PageRequest pageRequest) throws Exception;

	ArrayList<Transaction> getTransactionsForAccount(String accountNumber) throws Exception;

	List<Object> getDashboardTransactions(String queryParam);

}