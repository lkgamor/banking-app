package com.louisga.banking.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.louisga.banking.service.EmployeeService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/employees")
public class EmployeeRestController {

	private final EmployeeService employeeService;
	
	/**
	 * GET api/v1/employees/total retrieve total number of active employees
	 * @return  ResponseEntity<Long>
	 * @throws Exception 
	 */
	@GetMapping("/total")
	@ApiOperation(value = "Finds total number of employees.",
	  notes = "This API returns the total number of employees with this company.")
	public ResponseEntity<Long> getTotalEmployees() throws Exception {
		return new ResponseEntity<>(employeeService.getTotalEmployees(), HttpStatus.OK);
	}	
}
