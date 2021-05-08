package com.louisga.banking.repository;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.louisga.banking.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	Optional<Employee> findByEmployeeId(String id);

    @Query(value = "SELECT e FROM Employee e WHERE e.dateCreated BETWEEN :startDate AND :endDate")
	Page<Employee> findAllEmployeesByDate(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate);

}
