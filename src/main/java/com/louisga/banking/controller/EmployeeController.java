package com.louisga.banking.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.louisga.banking.model.Employee;
import com.louisga.banking.service.BranchService;
import com.louisga.banking.service.EmployeeService;
import com.louisga.banking.service.RoleService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

	private final RoleService roleService;
	private final BranchService branchService;	
	private final EmployeeService employeeService;

	/**
	 * 
	 * @param model
	 * @param assembler
	 * @param user
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/employees")
	String getEmployeeManagementPage(Model model, 
			  PagedResourcesAssembler<Employee> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "employeeLastName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception {
		Page<Employee> employeeList = employeeService.getPagedEmployees(PageRequest.of(page, size, Direction.ASC, sort), daterange);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ROLES, roleService.getAllRoles());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_BRANCHES, branchService.getBranches());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, employeeList.getTotalElements());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_EMPLOYEES, assembler.toModel(employeeList));
		return "pages/employees/index";
	}
}
