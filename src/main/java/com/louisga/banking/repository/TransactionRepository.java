package com.louisga.banking.repository;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.louisga.banking.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	
	@Query(value = "SELECT t FROM Transaction t WHERE t.transactionId = :transactionId")
	Optional<Transaction> findTransactionById(@Param("transactionId") String id);

	/** FOR ADMINS AND MANAGERS */
	@Query(value = "SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
	Page<Transaction> findAllTransactionsByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);

	@Query(value = "SELECT t FROM Transaction t ORDER BY t.transactionDate DESC")
	Page<Transaction> findRecentTransactions(Pageable pageable);

	@Query(value = "SELECT t FROM Transaction t WHERE t.transactionId LIKE %:transactionSearchParam% "
			+ "OR t.transactionAccountName LIKE %:transactionSearchParam%")
	Page<Transaction> findTransactionsMatching(String transactionSearchParam, Pageable pageable);
	
	/** FOR ALL GROUPS */	
	@Query(value = "SELECT t FROM Transaction t WHERE transactionAccountNumber = :accountNumber")
	Page<Transaction> findAllPagedTransactionsForAccount(String accountNumber, Pageable pageable);
	
	@Query(value = "SELECT t FROM Transaction t WHERE transactionAccountNumber = :accountNumber")
	ArrayList<Transaction> findAllTransactionsForAccount(String accountNumber);

	@Query(value = "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate < :now")
	Long findTotalTransactionsMadeToday(ZonedDateTime now);

	Page<Transaction> findTransactionByTransactionType(String string, Pageable pageable);
}
