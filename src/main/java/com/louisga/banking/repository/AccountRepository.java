package com.louisga.banking.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.louisga.banking.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Account a SET a.accountContact =:accountContact, "
    		+ "a.accountEmail =:accountEmail, a.accountOccupation =:accountOccupation WHERE a.accountNumber = :accountNumber")
	void updateAccount(@Param("accountContact") String accountContact, @Param("accountEmail") String accountEmail,
			@Param("accountOccupation") String accountOccupation, @Param("accountNumber") String accountNumber);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Account a SET a.accountBalance =:accountBalance WHERE a.accountNumber = :accountNumber")
	void setNewAccountBalance(@Param("accountNumber") String accountNumber, @Param("accountBalance") Double newAccountBalance);
	
    @Query(value = "SELECT accountBalance FROM Account WHERE accountNumber = :accountNumber")
	Double getAccountBalance(@Param("accountNumber") String accountNumber);
	
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Account a SET a.accountOwing =:accountOwing WHERE a.accountNumber = :accountNumber")
	void updateLoanStatus(@Param("accountNumber") String loanAccountNumber, @Param("accountOwing") boolean b);

    @Query(value = "SELECT accountStatus FROM Account WHERE accountNumber = :accountNumber")
	Boolean getAccountStatus(@Param("accountNumber") String accountNumber);

    @Query(value = "SELECT accountOwing FROM Account WHERE accountNumber = :accountNumber")
	Boolean getAccountLoanStatus(@Param("accountNumber") String accountNumber);
    
    @Query(value = "SELECT accountImage FROM Account WHERE accountNumber = :accountNumber")
   	byte[] getAccountImage(@Param("accountNumber") String accountNumber);
    
    @Query(value = "SELECT accountSignature FROM Account WHERE accountNumber = :accountNumber")
   	byte[] getAccountSignature(@Param("accountNumber") String accountNumber);

	@Query(value = "SELECT a FROM Account a")
	Page<Account> findAllAccounts(Pageable pageable);

	@Query(value = "SELECT a FROM Account a WHERE a.accountDateOpened BETWEEN :startDate AND :endDate")
	Page<Account> findAllAccountsByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);

	@Query(value = "SELECT a FROM Account a WHERE a.accountStatus = true")
	Page<Account> findAllActiveAccounts(Pageable pageable);
	
	@Query(value = "SELECT a FROM Account a WHERE a.accountStatus = true AND a.accountDateOpened BETWEEN :startDate AND :endDate")
	Page<Account> findAllActiveAccountsByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);

	@Query(value = "SELECT a FROM Account a WHERE a.accountNumber LIKE %:accountSearchParam% OR a.accountName LIKE %:accountSearchParam%  OR a.accountContact LIKE %:accountSearchParam%")
	Page<Account> findAccountsMatching(String accountSearchParam, Pageable pageable);
	
	@Query(value = "SELECT a FROM Account a WHERE accountNumber = :accountNumber")
	Optional<Account> findByAccountNumber(String accountNumber);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Account a SET a.accountDateClosed =:zonedDateTime, a.accountStatus =:status WHERE a.accountNumber = :accountNumber")
	void closeAccount(String accountNumber, ZonedDateTime zonedDateTime, Boolean status);

	@Query(value = "SELECT a FROM Account a WHERE a.accountStatus = true")
	List<Account> findAllActiveAccounts();

	@Query(value = "UPDATE Account a SET a.accountSignature = :newAccountSignature WHERE a.accountNumber = :accountNumber")
	void updateAccountSignature(String accountNumber, byte[] newAccountSignature);
	
	@Query(value = "UPDATE Account a SET a.accountImage = :newAccountImage WHERE a.accountNumber = :accountNumber")
	void updateAccountImage(String accountNumber, byte[] newAccountImage);

	@Query(value = "SELECT COUNT(a) FROM Account a WHERE a.accountStatus = true")
	Long countActiveAccounts();

	@Query(value = "SELECT accountEmail FROM Account WHERE accountNumber = :accountNumber")
	String findAccountEmailAddress(String accountNumber);
	
}
