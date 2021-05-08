package com.louisga.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.Employee;

import javassist.NotFoundException;

public interface EmployeeService {

	Long getTotalEmployees() throws Exception;

	Employee getEmployeeDetails(String employeeId) throws NotFoundException;

	Page<Employee> getPagedEmployees(PageRequest pageRequest, String daterange) throws Exception;

}