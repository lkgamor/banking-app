package com.louisga.banking.repository;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.louisga.banking.model.Deposit;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {

	@Query(value = "SELECT d FROM Deposit d WHERE depositedAccountNumber = :accountNumber")
	Page<Deposit> findAllPagedDepositsForAccount(String accountNumber, Pageable pageable);

	@Query(value = "SELECT d FROM Deposit d WHERE d.depositDate BETWEEN :startDate AND :endDate")
	Page<Deposit> findAllDepositsByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);

	@Query(value = "SELECT d FROM Deposit d WHERE depositedAccountNumber = :accountNumber")
	ArrayList<Deposit> findAllDepositsForAccount(String accountNumber);
	
	@Query(value = "SELECT COUNT(d) FROM Deposit d WHERE WEEKDAY(d.depositDate) = WEEKDAY(CURDATE())")
	List<Long> findTotalDepositsMadeToday();
	
	@Query(value= "SELECT COUNT(d) FROM Deposit d WHERE WEEKOFYEAR(d.depositDate)= WEEKOFYEAR(CURDATE()) AND WEEKDAY(d.depositDate) BETWEEN 1 AND 5 GROUP BY DAYOFWEEK(d.depositDate)")
	List<Long> findTotalDepositsMadeThisWeek();
	
	@Query(value= "SELECT COUNT(d) FROM Deposit d WHERE MONTH(d.depositDate) = MONTH(CURRENT_DATE()) AND YEAR(d.depositDate) = YEAR(CURRENT_DATE()) GROUP BY WEEK(d.depositDate)")
	List<Long> findTotalDepositsMadeThisMonth();
	
	@Query(value= "SELECT COUNT(d) FROM Deposit d WHERE YEAR(d.depositDate) = YEAR(CURDATE()) GROUP BY MONTH(d.depositDate)")
	List<Long> findTotalDepositsMadeThisYear();

	@Query(value = "SELECT d FROM Deposit d WHERE d.depositId LIKE %:depositSearchParam% "
			+ "OR d.depositedAccountName LIKE %:depositSearchParam% "
			+ "OR d.depositedAccountNumber LIKE %:depositSearchParam%")
	Page<Deposit> findDepositsMatching(String depositSearchParam, Pageable pageable);
}
