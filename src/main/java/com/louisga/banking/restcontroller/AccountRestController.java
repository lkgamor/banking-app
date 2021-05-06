package com.louisga.banking.restcontroller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.louisga.banking.model.Account;
import com.louisga.banking.model.AccountDTO;
import com.louisga.banking.model.AccountToUIDTO;
import com.louisga.banking.model.Deposit;
import com.louisga.banking.model.Loan;
import com.louisga.banking.model.Transaction;
import com.louisga.banking.model.Withdrawal;
import com.louisga.banking.service.AccountService;
import com.louisga.banking.service.TransactionService;
import com.louisga.banking.utility.AppConstants;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountRestController {

	private final AccountService accountService;
	private final TransactionService transactionService;

	/**
	 * GET api/v1/accounts retrieve all accounts
	 * @return
	 * @throws Exception 
	 */
	@GetMapping
	@ApiOperation(value = "Finds all accounts.",
	  notes = "This API returns all accounts opened with this company.")
	public ResponseEntity<List<Account>> getAllAccounts() throws Exception {
		return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/accounts/page retrieve page of all accounts
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/page")
	@ApiOperation(value = "Finds all paged accounts.",
	  notes = "This API returns all accounts opened with this company in a paginated form.")
	public ResponseEntity<?> getAllPagedAccount(PagedResourcesAssembler<Account> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "accountName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception{
		
		Page<Account> accounts = accountService.getPagedAccounts(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		
		return new ResponseEntity<>(assembler.toModel(accounts), HttpStatus.OK);
	}

	/**
	 * GET api/v1/accounts/search retrieve all accounts based on search parameters
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param accountSearchParam
	 * @param size
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/search")
	@ApiOperation(value = "Finds a number of accounts based on the search query param.",
	  notes = "This API returns a specific account or number of accounts opened with this company. Results will depend on how many records specified.")
	public ResponseEntity<?> getAccountBySearchParam(PagedResourcesAssembler<Account> assembler,  
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "accountName") String sort,
			  @RequestParam(name= "q", defaultValue = "") String accountSearchParam, 
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size) throws Exception{
		
		Page<Account> accounts = accountService.getAllAccountsBySearchParams(accountSearchParam, PageRequest.of(page, size, Direction.DESC, sort));
		
		return new ResponseEntity<>(assembler.toModel(accounts), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/accounts/total retrieve total number of accounts
	 * @return 
	 * @throws Exception 
	 */
	@GetMapping("/total")
	@ApiOperation(value = "Finds total number of accounts.",
	  notes = "This API returns the total number of accounts opened with this company.")
	public ResponseEntity<Long> getTotalAccounts() throws Exception {
		return new ResponseEntity<>(accountService.getTotalAccounts(), HttpStatus.OK);
	}

	/**
	 * GET api/v1/accounts/active retrieve all active accounts
	 * @param user
	 * @return  ResponseEntity<List<Account>>
	 * @throws Exception 
	 */
	@GetMapping("/active")
	@ApiOperation(value = "Finds all active accounts.",
	  notes = "This API returns all accounts that are still active with this company.")
	public ResponseEntity<List<Account>> getAllActiveAccounts() throws Exception {
		return new ResponseEntity<>(accountService.getActiveAccounts(), HttpStatus.OK);
	}

	/**
	 * GET api/v1/accounts/:id retrieve an account
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "Find an account.",
	  notes = "Provide an id to look up a specific account opened with this company.",
	  response = Account.class)
	public ResponseEntity<AccountToUIDTO> getAccount(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(accountService.getAccountDetails(id), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/accounts/:id/transactions retrieve an account
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/{id}/transactions")
	@ApiOperation(value = "Find account transactions.",
	  notes = "Provide an account id to look up all the transactions performed on this account")
	public ResponseEntity<ArrayList<Transaction>> getAccountTransactions(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(transactionService.getTransactionsForAccount(id), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/accounts/:id/loans retrieve an account
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/{id}/loans")
	@ApiOperation(value = "Find account loans.",
	  notes = "Provide an account id to look up all the loans taken by this account")
	public ResponseEntity<ArrayList<Loan>> getAccountLoans(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(transactionService.getLoansForAccount(id), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/accounts/:id/deposits retrieve an account
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/{id}/deposits")
	@ApiOperation(value = "Find account deposits.",
	  notes = "Provide an account id to look up all the deposits made by this account")
	public ResponseEntity<ArrayList<Deposit>> getAccountDeposit(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(transactionService.getDepositsForAccount(id), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/accounts/:id/withdrawals retrieve an account
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/{id}/withdrawals")
	@ApiOperation(value = "Find account withdrawals.",
	  notes = "Provide an account id to look up all the withdrawals made by this account")
	public ResponseEntity<ArrayList<Withdrawal>> getAccountWithdrawals(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(transactionService.getWithdrawalsForAccount(id), HttpStatus.OK);
	}

	/**
	 * POST api/v1/accounts create an account
	 * @param accountDTO
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@PostMapping
	@ApiOperation(value = "Create an account.",
	  notes = "Provide a payload to open an account with this company.",
	  response = Account.class)
	public ResponseEntity<Account> saveAccount(@RequestBody AccountDTO accountDTO) throws Exception {	
		return new ResponseEntity<>(accountService.saveAccount(accountDTO), HttpStatus.ACCEPTED);
	}

	/**
	 * PUT api/v1/accounts/:id update an account
	 * @param id
	 * @param account
	 * @return
	 * @throws Exception 
	 */
	@PutMapping("/{id}")
	@ApiOperation(value = "Update an account.",
	  notes = "Provide an id and a payload to look up and update the details of a specific account opened with this company.",
	  response = Account.class)
	public ResponseEntity<Account> updateAccount(@RequestBody AccountDTO account, @PathVariable String id) throws Exception {		
		return new ResponseEntity<>(accountService.updateAccount(id, account), HttpStatus.OK);
	}

	/**
	 * DELETE api/v1/accounts/:id delete an account
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Close an account.",
	  notes = "Provide an id to close a specific account with this company. Account status will be set to 'closed'",
	  response = String.class)
	public ResponseEntity<String> deleteAccount(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(accountService.deleteAccount(id), HttpStatus.ACCEPTED);
	}

}
