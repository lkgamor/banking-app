package com.louisga.banking.repository;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.louisga.banking.model.Withdrawal;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {

	@Query(value = "SELECT w FROM Withdrawal w WHERE withdrawalAccountNumber = :accountNumber")
	Page<Withdrawal> findAllPagedWithdrawalsForAccount(String accountNumber, Pageable pageable);

	@Query(value = "SELECT w FROM Withdrawal w WHERE w.withdrawalDate BETWEEN :startDate AND :endDate")
	Page<Withdrawal> findAllWithdrawalsByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);
	
	@Query(value = "SELECT w FROM Withdrawal w WHERE withdrawalAccountNumber = :accountNumber")
	ArrayList<Withdrawal> findAllWithdrawalsForAccount(String accountNumber);

	@Query(value = "SELECT COUNT(w) FROM Withdrawal w WHERE WEEKDAY(w.withdrawalDate) = WEEKDAY(CURDATE())")
	List<Long> findTotalWithdrawalsMadeToday();
	
	@Query(value= "SELECT COUNT(w) FROM Withdrawal w WHERE WEEKOFYEAR(w.withdrawalDate)=WEEKOFYEAR(CURDATE()) AND WEEKDAY(w.withdrawalDate) BETWEEN 1 AND 5 GROUP BY DAYOFWEEK(w.withdrawalDate)")
	List<Long> findTotalWithdrawalsMadeThisWeek();
	
	@Query(value= "SELECT COUNT(w) FROM Withdrawal w WHERE MONTH(w.withdrawalDate) = MONTH(CURRENT_DATE()) AND YEAR(w.withdrawalDate) = YEAR(CURRENT_DATE()) GROUP BY WEEK(w.withdrawalDate)")
	List<Long> findTotalWithdrawalsMadeThisMonth();
	
	@Query(value= "SELECT COUNT(w) FROM Withdrawal w WHERE YEAR(w.withdrawalDate) = YEAR(CURDATE()) GROUP BY MONTH(w.withdrawalDate)")
	List<Long> findTotalWithdrawalsMadeThisYear();
	
	@Query(value = "SELECT w FROM Withdrawal w WHERE w.withdrawalId LIKE %:withdrawalSearchParam% "
			+ "OR w.withdrawalAccountName LIKE %:withdrawalSearchParam% "
			+ "OR w.withdrawalAccountNumber LIKE %:withdrawalSearchParam%")
	Page<Withdrawal> findWithdrawalsMatching(String withdrawalSearchParam, Pageable pageable);
}
