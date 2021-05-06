package com.louisga.banking.restcontroller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.louisga.banking.model.Deposit;
import com.louisga.banking.model.Loan;
import com.louisga.banking.model.LoanToPay;
import com.louisga.banking.model.Transaction;
import com.louisga.banking.model.TransactionDTO;
import com.louisga.banking.model.TransactionResponse;
import com.louisga.banking.model.Withdrawal;
import com.louisga.banking.service.TransactionService;
import com.louisga.banking.utility.AppConstants;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class TransactionRestController {

	private final TransactionService transactionService;
	
	/**
	 * GET api/v1/loans retrieve all loans
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/loans")
	@ApiOperation(value = "Finds all loans.",
	  notes = "This API returns all loans taken in this company. Results will depend on who is requesting the resources.")
	public ResponseEntity<List<Loan>> getAllLoans() throws Exception {
		return new ResponseEntity<>(transactionService.getLoans(), HttpStatus.OK);	
	}
	
	/**
	 * GET api/v1/loans/page retrieve page of all loans
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/loans/page")
	@ApiOperation(value = "Finds all paged loans.",
	  notes = "This API returns all loans made with this company in a paginated form. Results will depend on who is requesting the resources.")
	public ResponseEntity<?> getAllPagedLoans(
			  PagedResourcesAssembler<Loan> assembler,  
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "loanAccountName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception{
		Page<Loan> loanList = transactionService.getPagedLoans(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		return new ResponseEntity<>(assembler.toModel(loanList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/loans/search retrieve all loans based on search parameters
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param loanId
	 * @param size
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/loans/search")
	@ApiOperation(value = "Finds a number of loans based on the search query param.",
	  notes = "This API returns a specific number of recent loans made with this company. Results will depend on who is "
	  		+ "requesting the resources and how many records they need.")
	public ResponseEntity<?> getLoansBySearchParam(
			  PagedResourcesAssembler<Loan> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "loanAccountName") String sort,
			  @RequestParam(name= "q", defaultValue = "") String loanId, 
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size) throws Exception{
		Page<Loan> loanList = transactionService.getAllLoansBySearchParams(loanId, PageRequest.of(page, size, Direction.DESC, sort));
		return new ResponseEntity<>(assembler.toModel(loanList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/loans/total retrieve total number of loans
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/loans/total")
	@ApiOperation(value = "Finds total number of loans.",
	  notes = "This API returns the total number of loans with this company.")
	public ResponseEntity<Long> getTotalLoans() throws Exception {
		return new ResponseEntity<>(transactionService.getTotalLoans(), HttpStatus.OK);
	}
	
	/**
	 * PUT api/v1/loans/:loanId update a loan
	 * @param user
	 * @param loanId
	 * @param loan
	 * @return
	 * @throws Exception
	 */
	@PutMapping("/loans/{loanId}")
	@ApiOperation(value = "Pay back a loan.",
	  notes = "Provide an id to look up a specific loan taken with this company and update its status to 'paid'.",
	  response = Double.class)
	public ResponseEntity<Double> confirmLoanPayment(@PathVariable String loanId, @RequestBody LoanToPay loan) throws Exception {
		String userId =  "53f93ef0-0763-42a0-b236-6b730a6965a4";
		return new ResponseEntity<>(transactionService.makeLoanPayment(userId, loanId, loan), HttpStatus.OK);
	}
		
	/**
	 * GET api/v1/deposits retrieve all deposits
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/deposits")
	@ApiOperation(value = "Finds all deposits.",
	  notes = "This API returns all deposits made with this company. Results will depend on who is requesting the resources.")
	public ResponseEntity<List<Deposit>> getAllDeposits() throws Exception {
		return new ResponseEntity<>(transactionService.getDeposits(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/deposits/page retrieve page of all deposits
	 * @param user
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/deposits/page")
	@ApiOperation(value = "Finds all paged deposits.",
	  notes = "This API returns all deposits made with this company in a paginated form. Results will depend on who is requesting the resources.")
	public ResponseEntity<?> getAllPagedDeposits(
			  PagedResourcesAssembler<Deposit> assembler,  
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "depositedAccountName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange,
			  @CookieValue(value=AppConstants.FILTER_BY_EMPLOYEE, required=false, defaultValue="") String employeeToFilterBy) throws Exception{
		Page<Deposit> depositList = transactionService.getPagedDeposits(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		return new ResponseEntity<>(assembler.toModel(depositList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/deposits/recent retrieve all deposits based on search parameters
	 * @param user
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param depositId
	 * @param size
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/deposits/search")
	@ApiOperation(value = "Finds a number of deposits based on the search query param.",
	  notes = "This API returns a specific number of recent deposits made with this company. Results will depend on who is "
	  		+ "requesting the resources and how many records they need.")
	public ResponseEntity<?> getDepositsBySearchParam( 
			  PagedResourcesAssembler<Deposit> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "depositedAccountName") String sort,
			  @RequestParam(name= "q", defaultValue = "") String depositId, 
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size) throws Exception{
		
		Page<Deposit> depositList = transactionService.getAllDepositsBySearchParams(depositId, PageRequest.of(page, size, Direction.DESC, sort));
		return new ResponseEntity<>(assembler.toModel(depositList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/deposits/total retrieve total number of loans
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/deposits/total")
	@ApiOperation(value = "Finds total number of deposits.",
	  notes = "This API returns the total number of deposits with this company.")
	public ResponseEntity<Long> getTotalDeposits() throws Exception {
		return new ResponseEntity<>(transactionService.getTotalDeposits(), HttpStatus.OK);
	}
		
	/**
	 * GET api/v1/withdrawals retrieve all withdrawals
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/withdrawals")
	@ApiOperation(value = "Finds all withdrawals.",
	  notes = "This API returns all withdrawals made from this company. Results will depend on who is requesting the resources.")
	public ResponseEntity<List<Withdrawal>> getAllWithdrawals() throws Exception {
		return new ResponseEntity<>(transactionService.getWithdrawals(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/withdrawals/page retrieve page of all withdrawals
	 * @param user
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/withdrawals/page")
	@ApiOperation(value = "Finds all paged withdrawals.",
	  notes = "This API returns all withdrawals made with this company in a paginated form. Results will depend on who is requesting the resources.")
	public ResponseEntity<?> getAllPagedWithdrawals( 
			  PagedResourcesAssembler<Withdrawal> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "withdrawalAccountName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception{
		Page<Withdrawal> withdrawalList = transactionService.getPagedWithdrawals(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		return new ResponseEntity<>(assembler.toModel(withdrawalList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/withdrawals/recent retrieve all withdrawals based on search parameters
	 * @param user
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param withdrawalId
	 * @param size
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/withdrawals/search")
	@ApiOperation(value = "Finds a number of withdrawals based on the search query param.",
	  notes = "This API returns a specific number of recent withdrawals made with this company. Results will depend on who is "
	  		+ "requesting the resources and how many records they need.")
	public ResponseEntity<?> getWithdrawalsBySearchParam(
			  PagedResourcesAssembler<Withdrawal> assembler,  
			  @RequestParam(defaultValue = "0") int page,
			  @RequestParam(defaultValue = "withdrawalAccountName") String sort,
			  @RequestParam(name= "q", defaultValue = "") String withdrawalId,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size) throws Exception{
		
		Page<Withdrawal> withdrawalList = transactionService.getAllWithdrawalsBySearchParams(withdrawalId, PageRequest.of(page, size, Direction.DESC, sort));
		return new ResponseEntity<>(assembler.toModel(withdrawalList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/withdrawals/total retrieve total number of withdrawals
	 * @return 
	 * @throws Exception 
	 */
	@GetMapping("/withdrawals/total")
	@ApiOperation(value = "Finds total number of withdrawals.",
	  notes = "This API returns the total number of withdrawals with this company.")
	public ResponseEntity<Long> getTotalWithdrawals() throws Exception {
		return new ResponseEntity<>(transactionService.getTotalWithdrawals(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions retrieve all transactions
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/transactions")
	@ApiOperation(value = "Finds all transactions.",
	  notes = "This API returns all transactions made with this company. Results will depend on who is requesting the resources.")
	public ResponseEntity<List<Transaction>> getAllTransactions() throws Exception{
		return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions/page retrieve page of all transactions
	 * @param user
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param size
	 * @param daterange
	 * @param employeeToFilterBy
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/transactions/page")
	@ApiOperation(value = "Finds all paged transactions.",
	  notes = "This API returns all transactions made with this company in a paginated form. Results will depend on who is requesting the resources.")
	public ResponseEntity<?> getAllPagedTransactions( 
			  PagedResourcesAssembler<Transaction> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "transactionAccountName") String sort,
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size,
			  @CookieValue(value=AppConstants.FILTER_BY_DATE, required=false, defaultValue="") String daterange) throws Exception{
		Page<Transaction> transactionList = transactionService.getPagedTransactions(PageRequest.of(page, size, Direction.DESC, sort), daterange);
		return new ResponseEntity<>(assembler.toModel(transactionList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions/recent retrieve all transactions based on search parameters
	 * @param user
	 * @param assembler
	 * @param page
	 * @param sort
	 * @param transactionId
	 * @param size
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/transactions/search")
	@ApiOperation(value = "Finds a number of transactions based on the search query param.",
	  notes = "This API returns a specific number of recent transactions made with this company. Results will depend on who is "
	  		+ "requesting the resources and how many records they need.")
	public ResponseEntity<?> getTransactionsBySearchParam( 
			  PagedResourcesAssembler<Transaction> assembler, 
			  @RequestParam(defaultValue = "0") int page, 
			  @RequestParam(defaultValue = "transactionAccountName") String sort,
			  @RequestParam(name= "q", defaultValue = "") String transactionId, 
			  @CookieValue(value=AppConstants.FILTER_BY_SIZE, required=false, defaultValue="10") Integer size) throws Exception{
		
		Page<Transaction> transactionList = transactionService.getAllTransactionsBySearchParams(transactionId, PageRequest.of(page, size, Direction.DESC, sort));
		return new ResponseEntity<>(assembler.toModel(transactionList), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions/recent retrieve all transactions
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/transactions/recent")
	@ApiOperation(value = "Finds a number of recent transactions based on the query param.",
	  notes = "This API returns a specific number of recent transactions made with this company. Results will depend on who is "
	  		+ "requesting the resources and how many records they need.")
	public ResponseEntity<Page<Transaction>> getRecentTransactions() throws Exception{
		return new ResponseEntity<>(transactionService.getRecentTransactions(PageRequest.of(0, 20, Direction.DESC, "transactionDate")), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions/total retrieve total number of transactions
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/transactions/total")
	@ApiOperation(value = "Finds total number of transactions.",
	  notes = "This API returns the total number of transactions with this company.")
	public ResponseEntity<Long> getTotalTransactions() throws Exception {
		return new ResponseEntity<>(transactionService.getTotalTransactions(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions/graph retrieve total number of transactions
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/transactions/graph")
	@ApiOperation(value = "Finds total number of transactions based on user defined filter.",
	  notes = "This API returns the total number of transactions for the dashboard graph.")
	public ResponseEntity<List<Object>> getTotalTransactionsForGraph( @RequestParam(name= "q", defaultValue = "all") String queryParam) throws Exception {
		return new ResponseEntity<>(transactionService.getDashboardTransactions(queryParam), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/transactions/:id retrieve a transaction
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/transactions/{id}")
	@ApiOperation(value = "Find a transaction.",
	  notes = "Provide an id to look up a specific transaction made with this company.",
	  response = Transaction.class)
	public ResponseEntity<Transaction> getTransaction(@PathVariable String id) throws Exception {
		return new ResponseEntity<>(transactionService.getTransactionDetails(id), HttpStatus.OK);
	}
	
	/**
	 * POST api/v1/transactions create a transaction
	 * @param transactionDTO
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("/transactions")
	@ApiOperation(value = "Create an transaction.",
	  notes = "Provide a payload to make a transaction with this company.",
	  response = TransactionResponse.class)
	public ResponseEntity<TransactionResponse> saveTransaction( @RequestBody final TransactionDTO transactionDTO) throws Exception{    	
		String userId = "53f93ef0-0763-42a0-b236-6b730a6965a4";
		return new ResponseEntity<>(transactionService.saveTransaction(userId, transactionDTO), HttpStatus.ACCEPTED);
	}	
}
