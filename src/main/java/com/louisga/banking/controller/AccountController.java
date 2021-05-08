package com.louisga.banking.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.model.Account;
import com.louisga.banking.model.AccountToUIDTO;
import com.louisga.banking.model.Branch;
import com.louisga.banking.model.CardType;
import com.louisga.banking.model.Employee;
import com.louisga.banking.service.AccountAuditService;
import com.louisga.banking.service.AccountService;
import com.louisga.banking.service.BranchService;
import com.louisga.banking.service.CardService;
import com.louisga.banking.service.EmployeeService;
import com.louisga.banking.service.TransactionAuditService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {

	private final CardService cardService;	
	private final BranchService branchService;	
	private final AccountService accountService;
	private final EmployeeService employeeService;
	private final CompanyConfiguration companyConfig;
	private final AccountAuditService accountAuditsService;
	private final TransactionAuditService transactionAuditsService;

	private List<String> genderList = List.of("Male", "Female");
	
	/**
	 * 
	 * @param model
	 * @param assembler
	 * @param user
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/accounts")
	String getAccountsPage(Model model,
			  PagedResourcesAssembler<Account> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "accountName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception {

		Page<Account> accountsList = accountService.getPagedAccounts(PageRequest.of(page, size, Direction.ASC, sort), daterange);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, accountsList.getTotalElements());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNTS, assembler.toModel(accountsList));
		return "pages/accounts/index";
	}

	/**
	 * 
	 * @param model
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/accounts/add")
	String getAddAccountDetailsPage(Model model) throws Exception {

		List<CardType> cards = cardService.getCards();	
		List<Branch> branches = branchService.getBranches();	
		Employee employee = employeeService.getEmployeeDetails(companyConfig.getDefaultUserId());

		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_CARDS, cards);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_EMPLOYEE, employee);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_BRANCHES, branches);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_GENDERS, genderList);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_TITLE, "New Account");
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_NAME, "New Account");
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT, new AccountToUIDTO());
		return "pages/accounts/form";
	}
	
	/**
	 * 
	 * @param accountNumber
	 * @param model
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/accounts/{accountNumber}")
	String getAccountDetailsPage(@PathVariable String accountNumber, Model model) throws Exception	{
		List<CardType> cards = cardService.getCards();
		List<Branch> branches = branchService.getBranches();	
		Employee employee = employeeService.getEmployeeDetails(companyConfig.getDefaultUserId());	
		AccountToUIDTO account =  accountService.getAccountDetails(accountNumber);
			
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_CARDS, cards);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT, account);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_EMPLOYEE, employee);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_BRANCHES, branches);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_GENDERS, genderList);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_TITLE, account.getAccountName());
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_COMPANY_NAME, companyConfig.getName());
		
		if(account.getAccountOwing())
			model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_NAME, "<i class='fas fa-money-check-alt'></i>"
					.concat(account.getAccountName())
					.concat(" | ").concat(accountNumber)
					.concat("<i class='fas fa-flag ml-3 text-danger' data-toggle='tooltip' data-placement='right' data-title='This account has Loan Arrears'></i>"));
		else 
			model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_NAME, "<i class='fas fa-money-check-alt'></i>"
					.concat(account.getAccountName())
					.concat(" | ").concat(accountNumber));
		return "pages/accounts/form";
	}

	/**
	 * 
	 * @param model
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/audits/accounts")
	String getAllAccountAudits(Model model, @RequestParam(defaultValue = "0") int page) throws Exception {
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_AUDITS, accountAuditsService.getPagedAccountAudits(PageRequest.of(page, 10)));
		return "pages/transactions/audits";
	}
	
	/**
	 * 
	 * @param model
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/audits/transactions")
	String getAllTransactionAudits(Model model, @RequestParam(defaultValue = "0") int page) throws Exception {
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTION_AUDITS, transactionAuditsService.getPagedTransactionAudits(PageRequest.of(page, 10)));
		return "pages/transactions/audits";
	}

}
