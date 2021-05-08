package com.louisga.banking.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.model.Employee;
import com.louisga.banking.repository.EmployeeRepository;
import com.louisga.banking.service.EmployeeService;
import com.louisga.banking.utility.AppConstants;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableAsync
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImplementation implements EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	private final CompanyConfiguration companyConfig;

	@Override
	public Long getTotalEmployees() throws Exception {
		try {
			log.info("{} ==> requested total number of employees", AppConstants.ANONYMOUS, LocalDateTime.now());
			return employeeRepository.count();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of employees failed", AppConstants.ANONYMOUS, LocalDateTime.now());
			throw new Exception("Could not fetch total number of employees from Database");
		}	
	}
	
	@Override
	public Employee getEmployeeDetails(String id) throws NotFoundException {
		Optional<Employee> existingEmployee = employeeRepository.findByEmployeeId(id);
		if(existingEmployee.isPresent()) {
			Employee employee = existingEmployee.get();
			String employeeFullName = employee.getEmployeeFirstName().concat(" ").concat(employee.getEmployeeLastName());
			
			log.info("{} ==> requested details of employee '{}'", AppConstants.ANONYMOUS, employeeFullName, LocalDateTime.now());
			return employee;
		} else {
			throw new NotFoundException("Employee with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Override
	public Page<Employee> getPagedEmployees(PageRequest pageRequest, String daterange) throws Exception {		
		try {
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all employees", AppConstants.ANONYMOUS, LocalDateTime.now());
				return employeeRepository.findAll(pageRequest);			
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all employees created between {} and {}", AppConstants.ANONYMOUS, startDate, endDate);
				return employeeRepository.findAllEmployeesByDate(pageRequest, startDate, endDate);		
			}
		} catch (Exception e) {
			log.error("{}'s ==> request to get a page of all active employees failed", AppConstants.ANONYMOUS, LocalDateTime.now());
			throw new Exception("Could not fetch terminated employees from Database");
		}
	}
}
