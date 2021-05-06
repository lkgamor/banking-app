package com.louisga.banking.repository;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.louisga.banking.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Integer> {

    @Query(value = "SELECT l FROM Loan l WHERE l.loanAccountNumber = :loanAccountNumber AND l.loanStatus =:loanStatus")
	Optional<Loan> getLoanAmountOfAccount(@Param("loanAccountNumber") String loanAccountNumber, @Param("loanStatus") boolean b);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Loan l SET l.loanStatus =:loanStatus WHERE l.loanId = :id")
	void updateLoanPaymentStatus(@Param("id") String id, @Param("loanStatus") boolean b);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Loan l SET l.loanPayed =:loanPayed, l.loanPayedDate = :loanPayedDate WHERE l.loanId = :id")
	void setLoanAmountPayed(@Param("id")String loanId, @Param("loanPayed") Double transactionAmount, ZonedDateTime loanPayedDate);

	@Query(value = "SELECT loanPayed FROM Loan WHERE loanId = :id")
	Double getLoanPayed(@Param("id")String loanId);

	@Query(value = "SELECT l FROM Loan l WHERE loanAccountNumber = :accountNumber")
	Page<Loan> findAllPagedLoansForAccount(String accountNumber, Pageable pageable);

	@Query(value = "SELECT l FROM Loan l WHERE l.loanDate BETWEEN :startDate AND :endDate")
	Page<Loan> findAllLoansByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);
	
	@Query(value = "SELECT l FROM Loan l WHERE l.employee.employeeId = :userId AND l.loanDate BETWEEN :startDate AND :endDate")
	Page<Loan> findAllLoansByDateForUser(Pageable pageable, String userId, ZonedDateTime startDate, ZonedDateTime endDate);

	@Query(value = "SELECT l FROM Loan l WHERE loanAccountNumber = :accountNumber")
	ArrayList<Loan> findAllLoansForAccount(String accountNumber);

	@Query(value = "SELECT COUNT(l) FROM Loan l WHERE WEEKDAY(l.loanDate) = WEEKDAY(CURDATE())")
	List<Long> findTotalLoansMadeToday();
	
	@Query(value= "SELECT COUNT(l) FROM Loan l WHERE WEEKOFYEAR(l.loanDate)=WEEKOFYEAR(CURDATE()) AND WEEKDAY(l.loanDate) BETWEEN 1 AND 5 GROUP BY DAYOFWEEK(l.loanDate)")
	List<Long> findTotalLoansMadeThisWeek();
	
	@Query(value= "SELECT COUNT(l) FROM Loan l WHERE MONTH(l.loanDate) = MONTH(CURRENT_DATE()) AND YEAR(l.loanDate) = YEAR(CURRENT_DATE()) GROUP BY WEEK(l.loanDate)")
	List<Long> findTotalLoansMadeThisMonth();
	
	@Query(value= "SELECT COUNT(l) FROM Loan l WHERE YEAR(l.loanDate) = YEAR(CURDATE()) GROUP BY MONTH(l.loanDate)")
	List<Long> findTotalLoansMadeThisYear();
	
	@Query(value = "SELECT l FROM Loan l WHERE l.loanId LIKE %:loanSearchParam% "
			+ "OR l.loanAccountName LIKE %:loanSearchParam% "
			+ "OR l.loanAccountNumber LIKE %:loanSearchParam%")
	Page<Loan> findLoansMatching(String loanSearchParam, Pageable pageable);
}
