package com.louisga.banking.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.model.AccountToUIDTO;
import com.louisga.banking.model.Branch;
import com.louisga.banking.model.Deposit;
import com.louisga.banking.model.EmailPayload;
import com.louisga.banking.model.Employee;
import com.louisga.banking.model.Loan;
import com.louisga.banking.model.LoanPayment;
import com.louisga.banking.model.LoanToPay;
import com.louisga.banking.model.Transaction;
import com.louisga.banking.model.TransactionDTO;
import com.louisga.banking.model.TransactionResponse;
import com.louisga.banking.model.TransactionsGraphData;
import com.louisga.banking.model.TransactionsGraphData.DepositGraphData;
import com.louisga.banking.model.TransactionsGraphData.LoanGraphData;
import com.louisga.banking.model.TransactionsGraphData.WithdrawalGraphData;
import com.louisga.banking.model.Withdrawal;
import com.louisga.banking.repository.AccountRepository;
import com.louisga.banking.repository.DepositRepository;
import com.louisga.banking.repository.LoanPaymentRepository;
import com.louisga.banking.repository.LoanRepository;
import com.louisga.banking.repository.TransactionRepository;
import com.louisga.banking.repository.WithdrawalRepository;
import com.louisga.banking.service.AccountService;
import com.louisga.banking.service.BranchService;
import com.louisga.banking.service.EmployeeService;
import com.louisga.banking.service.TransactionService;
import com.louisga.banking.utility.AppConstants;
import com.louisga.banking.utility.CentrifugoPublisher;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableAsync
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImplementation implements TransactionService {
	
	private final BranchService branchService;
	private final AccountService accountService;
	private final EmployeeService employeeService;
	
	private final LoanRepository loanRepository;
	private final LoanPaymentRepository loanPaymentRepository;
	private final DepositRepository depositRepository;
	private final AccountRepository accountRepository;
	private final WithdrawalRepository withdrawalRepository;
	private final TransactionRepository transactionRepository;
	
	private final CompanyConfiguration companyConfig;
	private final CentrifugoPublisher centrifugoPublisher;
	private final EmailServiceImplementation emailServiceImpl;
	//private final CustomTransactionMadeEventPublisher transactionEventPublisher;
	
	/**
	 * LOANS BUSINESS LOGIC	 
	 */
	@Override
	public Long getTotalLoans() throws Exception {
		try {
			log.info("{} ==> requested total number of loans", AppConstants.ANONYMOUS);
			return loanRepository.count();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of loans failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch total number of loans from Database");
		}	
	}
	
	@Override
	public ArrayList<Loan> getLoans() throws Exception {
		try {
			log.info("{} ==> requested all loans", AppConstants.ANONYMOUS);
			return (ArrayList<Loan>) loanRepository.findAll();
		} catch (Exception e) {
			log.info("{}'s ==> request to get all loans failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch loans from Database");
		}	
	}

	@Override
	public ArrayList<LoanPayment> getLoanPaymentsForLoan(String loanId) throws Exception {
		try {
			log.info("{} ==> requested all loan payment records for loan with id '{}'", AppConstants.ANONYMOUS, loanId);
			return (ArrayList<LoanPayment>) loanPaymentRepository.findByLoanIdOrderByLoanPaymentDateAsc(loanId);
		} catch (Exception e) {
			log.info("{}'s ==> request to get all loan payment records for loan with id '{}' failed", AppConstants.ANONYMOUS, loanId);
			throw new Exception("Could not fetch loan payment records for loan id [" + loanId + "]");
		}	
	}
	
	@Override
	public Loan getLoanForAccount(String accountNumber) throws NotFoundException {
		Optional<Loan> loan = loanRepository.getLoanAmountOfAccount(accountNumber, true);
		if(loan.isPresent()) {
			log.info("{} ==> requested details of loan for account '{}'", AppConstants.ANONYMOUS, loan.get().getLoanAccountName());
	    	return loan.get();
		} else {
			log.info("{}'s ==> request to get details of loan for account '{}' failed", AppConstants.ANONYMOUS, loan.get().getLoanAccountName());
			throw new NotFoundException("Account number ["+ accountNumber +"] does not have any loans with ".concat(companyConfig.getName()));
		}
	}
	
	@Override
	public void saveLoan(Loan loan) throws Exception {		
		try {
			loanRepository.save(loan);
			log.info("{} ==> issued a loan on account '{}'", AppConstants.ANONYMOUS, loan.getLoanAccountName());
		} catch (Exception e) {
			log.info("{}'s ==> request to issue a loan on account '{}' failed", AppConstants.ANONYMOUS, loan.getLoanAccountName());
			throw new Exception("Save Loan for account Number [" + loan.getLoanAccountNumber() + "] Failed");			
		}	
	}

	@Override
	public Page<Loan> getPagedLoans(PageRequest pageRequest, String daterange) throws Exception {		
		try {
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all loans", AppConstants.ANONYMOUS);
				return loanRepository.findAll(pageRequest);				
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all loans from {} to {}", AppConstants.ANONYMOUS, startDate, endDate);
				return loanRepository.findAllLoansByDate(pageRequest, startDate, endDate);		
			}
		} catch (Exception e) {
			log.info("{}'s ==> request to get a page of all loans", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch loan records");			
		}
	}
	
	@Override
	public Double makeLoanPayment(String loanId, LoanToPay loanToPay) throws Exception {
		
		Boolean loanStatus = false; //false means loan is not active i.e has been paid off
		String transactionAccountName = loanToPay.getLoanAccountName();
		String transactionAccountNumber = loanToPay.getLoanAccountNumber().toString();
		ZonedDateTime loanPaymentDate = ZonedDateTime.now();
		Double transactionAmount = loanToPay.getLoanAmountToPay();
		Double loanAmount = loanToPay.getLoanAmount();
		
		Double existingPaymentMade = getExistingPaymentAmount(loanId);
	    
		Employee paymentIssuedBy = employeeService.getEmployeeDetails(companyConfig.getDefaultUserId());
		
		Branch branch = paymentIssuedBy.getBranch();
		
		Double amountPayed = existingPaymentMade + transactionAmount;		
		Double amountInExcess = amountPayed - loanAmount;	
		Double amountLeftToBePayed = loanAmount - amountPayed;		
		
		if(Double.compare(amountPayed, loanAmount) == 0) {
			confirmAccountLoanRepayment(loanId, transactionAccountNumber, transactionAccountName, amountPayed, loanAmount, loanPaymentDate);
		} else if(Double.compare(amountPayed, loanAmount) > 0) {
			
			confirmAccountLoanRepayment(loanId, transactionAccountNumber, transactionAccountName, amountPayed, loanAmount, loanPaymentDate);
			
			//Save excess of loan payment as a deposit for the account user
			TransactionDTO excessLoanPaymentToSaveAsDeposit = new TransactionDTO();
			excessLoanPaymentToSaveAsDeposit.setTransactionAccountName(transactionAccountName);
			excessLoanPaymentToSaveAsDeposit.setTransactionAccountNumber(transactionAccountNumber);
			excessLoanPaymentToSaveAsDeposit.setTransactionType(AppConstants.TRANSACTION_TYPE_DEPOSIT);
			excessLoanPaymentToSaveAsDeposit.setTransactionAmount(amountInExcess);
			excessLoanPaymentToSaveAsDeposit.setTransactionIssuedBy(paymentIssuedBy.getEmployeeId());
			excessLoanPaymentToSaveAsDeposit.setTransactionBranch(branch.getBranchId());
			
			saveTransaction(excessLoanPaymentToSaveAsDeposit);

		} else {
			try {
				loanStatus = true;
				loanRepository.setLoanAmountPayed(loanId, amountPayed, loanPaymentDate);
				log.info("{} ==> has confirmed {}'s partial loan payment", AppConstants.ANONYMOUS, transactionAccountName);
			} catch (Exception e) {
				log.info("{}'s ==> request to confirm {}'s partial loan payment failed", AppConstants.ANONYMOUS, transactionAccountName);
				throw new Exception("Could not confirm partial loan payment for [" + transactionAccountNumber + "]");		
			}
		}	
		
		//Create a loan payment record in database
		try {			
			LoanPayment loanPayment = LoanToPay.toLoanPaymentFromLoanToPay(loanId, loanPaymentDate, transactionAmount, amountLeftToBePayed, paymentIssuedBy, loanStatus);
			loanPaymentRepository.save(loanPayment);
			log.info("{} ==> created a loan payment record for {}'s loan '{}'", AppConstants.ANONYMOUS, transactionAccountName, loanId);
		} catch (Exception e) {
			log.info("{}'s ==> request to created a loan payment record for {}'s loan '{}' failed", AppConstants.ANONYMOUS, transactionAccountName, loanId);
			throw new Exception("Could not create a loan payment record for [" + transactionAccountNumber + "]");		
		}
		
		return amountPayed;	
	}

	private void confirmAccountLoanRepayment(String loanId, String transactionAccountNumber, String transactionAccountName,
			Double amountPayed, Double loanAmount, ZonedDateTime loanPaymentDate) throws Exception {
		try {
			confirmLoanPayed(loanId, amountPayed, transactionAccountNumber, loanPaymentDate);
			log.info("{} ==> has confirmed {}'s loan payment", AppConstants.ANONYMOUS, transactionAccountName);
			
			//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
			if (companyConfig.getSendEmails()) {
				String accountEmail = accountService.getAccountEmail(transactionAccountNumber);
				
				//CHECK IF TRANSACTION ACCOUNT HAS AN EMAIL ADDRESS	
				if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
					String emailSubject = "LOAN FULLY PAID";
					String emailMessage = "Hello <em>" + transactionAccountName + "</em>! You have successfully paid off the loan of amount "
							+ "<strong>GH₵ " + loanAmount + "</strong> which you took on your account "
							+ "<strong>" + transactionAccountNumber + "</strong>. The Transaction Reference is "
							+ "<strong>" + loanId + "</strong> and payment date is"
							+ "<strong>" + loanPaymentDate + ".</strong> You are now eligible to take another loan. Thank you for saving with us.";
					buildEmail(accountEmail, emailSubject, emailMessage);
				}
			}
		} catch (Exception e) {
			log.info("{}'s ==> request to confirm {}'s loan payment failed", AppConstants.ANONYMOUS, transactionAccountName);
			throw new Exception("Could not confirm loan payment for [" + transactionAccountNumber + "]");		
		}
	}

	@Override
	public void confirmLoanPayed(String loanId, Double amountPayed, String accountNumber, ZonedDateTime loanPaymentDate) {
		loanRepository.setLoanAmountPayed(loanId, amountPayed, loanPaymentDate);
		loanRepository.updateLoanPaymentStatus(loanId, false);		
		accountRepository.updateLoanStatus(accountNumber, false);
	}

	private Double getExistingPaymentAmount(String loanId) {
		return loanRepository.getLoanPayed(loanId);
	}
	
	@Override
	public ArrayList<Loan> getLoansForAccount(String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested all loans for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return loanRepository.findAllLoansForAccount(accountNumber);	
		} catch (Exception e) {
			log.info("{}'s ==> request to get all loans for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch loan records for Account [" + accountNumber + "]");			
		}
	}	

	@Override
	public Page<Loan> getPagedLoansForAccount(PageRequest pageRequest, String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested a page of all loans for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return loanRepository.findAllPagedLoansForAccount(accountNumber, pageRequest);	
		} catch (Exception e) {
			log.info("{}'s ==> request to get a page of all loans for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch loan records for Account [" + accountNumber + "]");			
		}
	}

	@Override
	public Page<Loan> getAllLoansBySearchParams(String loanSearchParam, PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> searching for loan matching '{}'", AppConstants.ANONYMOUS, loanSearchParam);
			return loanRepository.findLoansMatching(loanSearchParam, pageRequest);
		} catch (Exception e) {
			log.info("{}'s ==> search for loan matching '{}' failed", AppConstants.ANONYMOUS, loanSearchParam);
			throw new Exception("Could not fetch loan records matching ["+ loanSearchParam + "]");	
		}
	}
	
	
	/**
	 * DEPOSITS BUSINESS LOGIC	 
	 */
	@Override
	public Long getTotalDeposits() throws Exception {
		try {
			log.info("{} ==> requested total number of deposits", AppConstants.ANONYMOUS);
			return depositRepository.count();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of deposits failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch total number of deposits from Database");
		}	
	}
	
	@Override
	public ArrayList<Deposit> getDeposits() throws java.lang.Exception {
		try {
			log.info("{} ==> requested all deposits", AppConstants.ANONYMOUS);
			return (ArrayList<Deposit>) depositRepository.findAll();
		} catch (Exception e) {
			log.info("{}'s ==> request to get all deposits failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch deposits from Database");
		}
	}
	
	@Override
	public ArrayList<Deposit> getDepositsForAccount(String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested all deposits for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return depositRepository.findAllDepositsForAccount(accountNumber);	
		} catch (Exception e) {
			log.info("{}'s ==> request to get all deposits for account '{}' failed", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch deposit records for Account [" + accountNumber + "]");			
		}
	}

	@Override
	public Page<Deposit> getPagedDeposits(PageRequest pageRequest, String daterange) throws Exception {		
		try {
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all deposits", AppConstants.ANONYMOUS);
				return depositRepository.findAll(pageRequest);		
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all deposits from {} to {}", AppConstants.ANONYMOUS, startDate, endDate);
				return depositRepository.findAllDepositsByDate(pageRequest, startDate, endDate);		
			}			
			
		} catch (Exception e) {
			log.info("{}'s ==> request to get a page of all deposits failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch deposits from Database");
		}
	}
	
	@Override
	public Page<Deposit> getPagedDepositsForAccount(PageRequest pageRequest, String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested a page of all deposits for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return depositRepository.findAllPagedDepositsForAccount(accountNumber, pageRequest);	
		} catch (Exception e) {
			log.info("{}'s ==> request to get a page of all deposits for account '{}' failed", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch deposit records for Account [" + accountNumber + "]");			
		}
	}

	@Override
	public Page<Deposit> getAllDepositsBySearchParams(String depositSearchParam, PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> searching for deposit matching '{}'", AppConstants.ANONYMOUS, depositSearchParam);
			return depositRepository.findDepositsMatching(depositSearchParam, pageRequest);
		} catch (Exception e) {
			log.info("{}'s ==> search for deposit matching '{}' failed", AppConstants.ANONYMOUS, depositSearchParam);
			throw new Exception("Could not fetch deposit records matching ["+ depositSearchParam + "]");	
		}
	}
	
	
	/**
	 * WITHDRAWALS BUSINESS LOGIC	 
	 */
	@Override
	public Long getTotalWithdrawals() throws Exception {
		try {
			log.info("{} ==> requested total number of withdrawals", AppConstants.ANONYMOUS);
			return withdrawalRepository.count();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of withdrawals failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch total number of withdrawals from Database");
		}	
	}
	
	@Override
	public ArrayList<Withdrawal> getWithdrawals() throws java.lang.Exception {
		try {
			log.info("{} ==> requested all withdrawals", AppConstants.ANONYMOUS);
			return (ArrayList<Withdrawal>) withdrawalRepository.findAll();
		} catch (Exception e) {
			log.info("{} ==> request to fetch all withdrawals failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch withdrawals from Database");
		}
	}
	
	@Override
	public ArrayList<Withdrawal> getWithdrawalsForAccount(String accountNumber) throws Exception {
		try {
			return withdrawalRepository.findAllWithdrawalsForAccount(accountNumber);	
		} catch (Exception e) {
			throw new Exception("No withdrawal records were found for Account [" + accountNumber + "]");			
		}
	}

	@Override
	public Page<Withdrawal> getPagedWithdrawals(PageRequest pageRequest, String daterange) throws java.lang.Exception {
		try {						
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all withdrawals", AppConstants.ANONYMOUS);
				return withdrawalRepository.findAll(pageRequest);	
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all withdrawals from {} to {}", AppConstants.ANONYMOUS, startDate, endDate);
				return withdrawalRepository.findAllWithdrawalsByDate(pageRequest, startDate, endDate);		
			}
			
		} catch (Exception e) {
			log.info("{'s} ==> request to get a page of all withdrawals failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch withdrawals from Database");
		}
	}
	
	@Override
	public Page<Withdrawal> getPagedWithdrawalsForAccount(PageRequest pageRequest, String accountNumber) throws Exception {
		try {
			return withdrawalRepository.findAllPagedWithdrawalsForAccount(accountNumber, pageRequest);	
		} catch (Exception e) {
			throw new Exception("No withdrawal records were found for Account [" + accountNumber + "]");			
		}
	}

	@Override
	public Page<Withdrawal> getAllWithdrawalsBySearchParams(String withdrawalSearchParam, PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> searching for withdrawal matching '{}'", AppConstants.ANONYMOUS, withdrawalSearchParam);
			return withdrawalRepository.findWithdrawalsMatching(withdrawalSearchParam, pageRequest);
		} catch (Exception e) {
			log.info("{}'s ==> search for withdrawal matching '{}' failed", AppConstants.ANONYMOUS, withdrawalSearchParam);
			throw new Exception("Could not fetch withdrawal records matching ["+ withdrawalSearchParam + "]");	
		}
	}
	
	
	/**
	 * GENERAL TRANSACTIONS BUSINESS LOGIC	 
	 */
	@Override
	public Long getTotalTransactions() throws Exception {
		try {
			log.info("{} ==> requested total number of transactions", AppConstants.ANONYMOUS);
			return transactionRepository.count();
		} catch (Exception e) {
			log.error("{}'s ==> request to get total number of transactions failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch total number of transactions from Database");
		}	
	}
	
	
	@Override
	public ArrayList<Transaction> getAllTransactions() throws java.lang.Exception {
		try {
			log.info("{} ==> requested all transactions", AppConstants.ANONYMOUS);
			return (ArrayList<Transaction>) transactionRepository.findAll();
		} catch (Exception e) {
			log.info("{}'s ==> request to fetch all transactions failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch transactions from Database");
		}
	}
	
	@Override
	public Transaction getTransactionDetails(String id) throws Exception {		
		Optional<Transaction> transaction = transactionRepository.findTransactionById(id);
		if(transaction.isPresent()) {
			String transactionId = transaction.get().getTransactionId();
	    	try {
				log.info("{} ==> requested details of transaction '{}'", AppConstants.ANONYMOUS, transactionId);
				return transaction.get();
			} catch (Exception e) {
				log.info("{}'s ==> request to get details of transaction '{}' failed", AppConstants.ANONYMOUS, transactionId);
				throw new Exception("Could not fetch transaction with id ["+ id +"] from Database");
			}
		} else {
			throw new NotFoundException("No transaction with id ["+ id +"] exists with ".concat(companyConfig.getName()));
		}
	}
	
	@Override
	public TransactionResponse saveTransaction(TransactionDTO transactionDTO) throws Exception {
		
		LocalDateTime now = LocalDateTime.now();
		ZonedDateTime transactionDate = ZonedDateTime.now();
	    AccountToUIDTO transactionAccount = accountService.getAccountDetails(transactionDTO.getTransactionAccountNumber());
	    Employee employee = employeeService.getEmployeeDetails(companyConfig.getDefaultUserId());
	    Branch branch = branchService.getBranchDetails(transactionDTO.getTransactionBranch());

		String transactionID = "";
	    String transactionAccountName = transactionDTO.getTransactionAccountName();
	    String transactionAccountNumber = transactionDTO.getTransactionAccountNumber();
	    Double transactionAmount = transactionDTO.getTransactionAmount();
	    String transactionType = transactionDTO.getTransactionType();

	    Transaction transaction = new Transaction();
		TransactionResponse response = new TransactionResponse();
	    
	    Double oldAccountBalance = transactionAccount.getAccountBalance();
	    Double newAccountBalance = 0.0;
	    if(transactionDTO.getTransactionType().equalsIgnoreCase("deposit")) {
	    	
	    	transactionID = companyConfig.getAccountNamesPrefix() + "-D-" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute() + now.getSecond();
	    	newAccountBalance = oldAccountBalance + transactionDTO.getTransactionAmount();	
		    accountService.updateAccountBalance(transactionDTO.getTransactionAccountNumber().toString(), newAccountBalance);
		    transaction.setTotalBalance(newAccountBalance);	 
			response.setAccountBalance(newAccountBalance);   	
			
	    } else if(transactionDTO.getTransactionType().equalsIgnoreCase("withdrawal")) {

	    	transactionID = companyConfig.getAccountNamesPrefix() + "-W-" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute() + now.getSecond();
	    	Double availableBalance = checkAvailableBalance(transactionDTO.getTransactionAccountNumber());
	    	
	    	if(availableBalance >= transactionDTO.getTransactionAmount()) {
	    		
	    		newAccountBalance = oldAccountBalance - transactionDTO.getTransactionAmount();
	    	    accountService.updateAccountBalance(transactionDTO.getTransactionAccountNumber().toString(), newAccountBalance);
	    	    transaction.setTotalBalance(newAccountBalance);
				response.setAccountBalance(newAccountBalance);
		    	
	    	} else {
	    		throw new Exception(transactionDTO.getTransactionAccountName().concat(" does not have enough savings to make a withdrawal"));
	    	}
	    	
	    } else if(transactionDTO.getTransactionType().equalsIgnoreCase("loan")) {

	    	transactionID = companyConfig.getAccountNamesPrefix() + "-L-" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute() + now.getSecond();
	    	Boolean isOwing = checkOwing(transactionDTO.getTransactionAccountNumber());
	    	if(isOwing) {
	    		throw new Exception(transactionDTO.getTransactionAccountName().concat(" has loan arrears. Ensure full payment before issuing further loans!"));
	    	} else {
	    		
	    		Double availableBalance = checkAvailableBalance(transactionDTO.getTransactionAccountNumber());		    	
		    	if(availableBalance > 0) {
			    	transactionAccount.setAccountOwing(true);
		    	    accountService.updateAccountLoanStatus(transactionAccountNumber, true);
		    	    transaction.setTotalBalance(availableBalance);
					response.setAccountBalance(availableBalance);			    	
		    	} else {
		    		throw new Exception(transactionDTO.getTransactionAccountName().concat(" has no savings to guarantee a loan"));
		    	}		    	
	    	}	    	
	    }
	    
	    transaction.setTransactionId(transactionID);
	    transaction.setTransactionType(transactionType);
	    transaction.setTransactionDate(transactionDate);
	    transaction.setTransactionAmount(transactionAmount);
	    transaction.setTransactionAccountName(transactionAccountName);
	    transaction.setTransactionAccountNumber(transactionAccountNumber);
	    transaction.setEmployee(employee);
	    transaction.setBranch(branch);
	    
	    try {
			log.info("{} ==> created a transaction on account '{}'", AppConstants.ANONYMOUS, transactionDTO.getTransactionAccountName());
			transactionRepository.save(transaction);
			saveTransactionToApproriateTable(transaction);

			String employeeFullName = employee.getEmployeeFirstName().concat(" ").concat(employee.getEmployeeLastName());
			response.setTransactionId(transactionID);
			response.setTransactionAccountName(transactionAccountName);
			response.setTransactionAccountNumber(transactionAccountNumber);
			response.setTransactionAmount(transactionAmount);
			response.setTransactionType(transactionType);
			response.setTransactionDate(transactionDate);
			response.setTransactionIssuedBy(employeeFullName);
			response.setTransactionBranch(branch.getBranchName());
			
			try {
				centrifugoPublisher.sendTransactionMadeToCentrifugo(response);
				log.info("Sent {} transaction details to Centrifugo", transactionType);
				//transactionEventPublisher.publishTransactionMadeEvent(transactionType, transactionAmount, transactionAccountNumber, transactionDate, employeeFullName);
			} catch (Exception e) {
				  System.out.println(e);
				log.info("Failed to send {} transaction details to Centrifugo", transactionType);
			}	

			//CHECK IF COMPANY HAS THE PERMISSION TO SEND MAILS		
			if (companyConfig.getSendEmails()) {
				String accountEmail = transactionAccount.getAccountEmail();
				
				//CHECK IF TRANSACTION ACCOUNT HAS AN EMAIL ADDRESS	
				if (!accountEmail.isBlank() || !accountEmail.isEmpty()) {
					String emailSubject = "TRANSACTION SUCCESSFUL";
					String emailMessage = "Hello <em>" + transactionAccount.getAccountName() + "</em>! Please be informed that a <strong>" 
							+  transactionType + "</strong> has been issued on your account <strong>" + transactionAccount.getAccountNumber()
							+ "</strong>. The Transaction Reference is <strong>" + transactionID
							+ "</strong> and your current total balance is now <strong>GH₵ "
							+ response.getAccountBalance()
							+ "</strong>. Thank you for saving with us.";
					buildEmail(accountEmail, emailSubject, emailMessage);
				}
			}
			
			return response;
		} catch (Exception e) {
			log.info("{}'s ==> request to create transaction on account '{}' failed", AppConstants.ANONYMOUS, transactionDTO.getTransactionAccountName());
			throw new Exception("Save Transaction [" + transactionID + "] Failed");			
		}			
		
	}

	private void saveTransactionToApproriateTable(Transaction transaction) throws Exception {
		
		switch(transaction.getTransactionType()) {
			
			case AppConstants.TRANSACTION_TYPE_LOAN :
				Loan loanToSave = new Loan();
				loanToSave.setLoanId(transaction.getTransactionId());
				loanToSave.setLoanDate(transaction.getTransactionDate());
				loanToSave.setLoanAmount(transaction.getTransactionAmount());
				loanToSave.setLoanAccountName(transaction.getTransactionAccountName());
				loanToSave.setLoanAccountNumber(transaction.getTransactionAccountNumber());
				loanToSave.setLoanStatus(true);
				loanToSave.setLoanPayed(0.00);
				loanToSave.setEmployee(transaction.getEmployee());
				loanToSave.setBranch(transaction.getBranch());
				try {
					loanRepository.save(loanToSave);
				} catch (Exception e) {
					log.info("{}'s ==> request to issue LOAN '{}' on account '{}' failed", AppConstants.ANONYMOUS, transaction.getTransactionId(), transaction.getTransactionAccountName());
					throw new Exception("Save Loan [" + transaction.getTransactionId() + "] Failed");		
				}
			break;
			
			case AppConstants.TRANSACTION_TYPE_DEPOSIT :
				Deposit depositToSave = new Deposit();
				depositToSave.setDepositId(transaction.getTransactionId());
				depositToSave.setDepositDate(transaction.getTransactionDate());
				depositToSave.setDepositAmount(transaction.getTransactionAmount());
				depositToSave.setTotalBalance(transaction.getTotalBalance());
				depositToSave.setDepositedAccountName(transaction.getTransactionAccountName());
				depositToSave.setDepositedAccountNumber(transaction.getTransactionAccountNumber());
				depositToSave.setEmployee(transaction.getEmployee());
				depositToSave.setBranch(transaction.getBranch());
				try {
					depositRepository.save(depositToSave);
				} catch (Exception e) {
					log.info("{}'s ==> request to issue DEPOSIT '{}' on account '{}' failed", AppConstants.ANONYMOUS, transaction.getTransactionId(), transaction.getTransactionAccountName());
					throw new Exception("Save Deposit [" + transaction.getTransactionId() + "] Failed");		
				}
			break;
				
			case AppConstants.TRANSACTION_TYPE_WITHDRAWAL :
				Withdrawal withdrawalToSave = new Withdrawal();
				withdrawalToSave.setWithdrawalId(transaction.getTransactionId());
				withdrawalToSave.setWithdrawalDate(transaction.getTransactionDate());
				withdrawalToSave.setWithdrawalAmount(transaction.getTransactionAmount());
				withdrawalToSave.setTotalBalance(transaction.getTotalBalance());
				withdrawalToSave.setWithdrawalAccountName(transaction.getTransactionAccountName());
				withdrawalToSave.setWithdrawalAccountNumber(transaction.getTransactionAccountNumber());
				withdrawalToSave.setEmployee(transaction.getEmployee());
				withdrawalToSave.setBranch(transaction.getBranch());
				try {
					withdrawalRepository.save(withdrawalToSave);
				} catch (Exception e) {
					log.info("{}'s ==> request to issue WITHDRAWAL '{}' on account '{}' failed", AppConstants.ANONYMOUS, transaction.getTransactionId(), transaction.getTransactionAccountName());
					throw new Exception("Save Withdrawal [" + transaction.getTransactionId() + "] Failed");		
				}
			break;
		}
		
		log.info("{} ==> issued {} '{}' on account '{}'", AppConstants.ANONYMOUS, transaction.getTransactionType(), transaction.getTransactionId(), transaction.getTransactionAccountName());

	}

	private Double checkAvailableBalance(String accountID) throws Exception {	
		return accountService.getAccountBalance(accountID);
	}

	private Boolean checkOwing(String accountID) throws Exception {		
		return accountService.getLoanStatus(accountID);		
	}	

	@Override
	public Page<Transaction> getPagedTransactions(PageRequest pageRequest, String daterange) throws Exception {
		try {
			if (daterange.isBlank() || daterange.isEmpty())	{
				log.info("{} ==> requested a page of all transactions", AppConstants.ANONYMOUS);
				return transactionRepository.findAll(pageRequest);		
			} else {
				ZonedDateTime startDate = ZonedDateTime.parse(daterange.split("_")[0]);
				ZonedDateTime endDate = ZonedDateTime.parse(daterange.split("_")[1]);
				log.info("{} ==> requested a page of all transactions from {} to {}", AppConstants.ANONYMOUS, startDate, endDate);
				return transactionRepository.findAllTransactionsByDate(pageRequest, startDate, endDate);		
			}
		} catch (Exception e) {
			log.info("{}'s ==> request to get a page of all transactions failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch transaction records from Database");			
		}
	}
	
	@Override
	public Page<Transaction> getPagedTransactionsForAccount(PageRequest pageRequest, String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested a page of all transactions for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return transactionRepository.findAllPagedTransactionsForAccount(accountNumber, pageRequest);	
		} catch (Exception e) {
			log.info("{}'s ==> request to get a page of all transactions for account '{}' failed", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch transaction records for Account [" + accountNumber + "]");			
		}
	}
	
	@Override
	public ArrayList<Transaction> getTransactionsForAccount(String accountNumber) throws Exception {
		try {
			log.info("{} ==> requested all transactions for account '{}'", AppConstants.ANONYMOUS, accountNumber);
			return transactionRepository.findAllTransactionsForAccount(accountNumber);	
		} catch (Exception e) {
			log.info("{}'s ==> request to get all transactions for account '{}' failed", AppConstants.ANONYMOUS, accountNumber);
			throw new Exception("Could not fetch transaction records for Account [" + accountNumber + "]");			
		}
	}

	@Override
	public List<Object> getDashboardTransactions(String queryParam) {
		
		var loanGraphData = new TransactionsGraphData.LoanGraphData();		
		var depositGraphData = new TransactionsGraphData.DepositGraphData();		
		var withdrawalGraphData = new TransactionsGraphData.WithdrawalGraphData();
		
		switch (queryParam.toLowerCase()) {
			case AppConstants.FILTER_BY_DAY:
				List<Long> loansForToday = loanRepository.findTotalLoansMadeToday();
				List<Long> depositsForToday = depositRepository.findTotalDepositsMadeToday();
				List<Long> withdrawalsForToday = withdrawalRepository.findTotalWithdrawalsMadeToday();
				loanGraphData.setData(loansForToday);
				depositGraphData.setData(depositsForToday);
				withdrawalGraphData.setData(withdrawalsForToday);
			break;
			
			case AppConstants.FILTER_BY_WEEK:
				List<Long> loansForWeek = loanRepository.findTotalLoansMadeThisWeek();
				List<Long> depositsForWeek = depositRepository.findTotalDepositsMadeThisWeek();
				List<Long> withdrawalsForWeek = withdrawalRepository.findTotalWithdrawalsMadeThisWeek();
				loanGraphData.setData(loansForWeek);
				depositGraphData.setData(depositsForWeek);
				withdrawalGraphData.setData(withdrawalsForWeek);
			break;
				
			case AppConstants.FILTER_BY_MONTH:
				List<Long> loansForMonth = loanRepository.findTotalLoansMadeThisMonth();
				List<Long> depositsForMonth = depositRepository.findTotalDepositsMadeThisMonth();
				List<Long> withdrawalsForMonth = withdrawalRepository.findTotalWithdrawalsMadeThisMonth();
				loanGraphData.setData(loansForMonth);
				depositGraphData.setData(depositsForMonth);
				withdrawalGraphData.setData(withdrawalsForMonth);
			break;
				
			case AppConstants.FILTER_BY_YEAR:
				List<Long> loansForYear = loanRepository.findTotalLoansMadeThisYear();
				List<Long> depositsForYear = depositRepository.findTotalDepositsMadeThisYear();
				List<Long> withdrawalsForYear = withdrawalRepository.findTotalWithdrawalsMadeThisYear();
				loanGraphData.setData(loansForYear);
				depositGraphData.setData(depositsForYear);
				withdrawalGraphData.setData(withdrawalsForYear);
			break;
			
			case AppConstants.FILTER_BY_ALL:
			default:
				List<Long> allLoans = List.of(loanRepository.count());
				List<Long> allDeposits = List.of(depositRepository.count());
				List<Long> allWithdrawals = List.of(withdrawalRepository.count());
				loanGraphData.setData(allLoans);
				depositGraphData.setData(allDeposits);
				withdrawalGraphData.setData(allWithdrawals);
			break;
		}
		
		return buildGraphData(loanGraphData, depositGraphData, withdrawalGraphData);
	}

	private List<Object> buildGraphData(LoanGraphData loanGraphData, DepositGraphData depositGraphData,
			WithdrawalGraphData withdrawalGraphData) {
		var graphPayload = TransactionsGraphData.builder()
							.loansGraphData(loanGraphData)
							.depositGraphData(depositGraphData)
							.withdrawalGraphData(withdrawalGraphData)
							.build();
		
		return List.of(graphPayload.getLoansGraphData(), graphPayload.getDepositGraphData(), graphPayload.getWithdrawalGraphData());
	}
	
	
	@Async
	private void buildEmail( String emailAddress, String emailSubject, String emailMessage) throws UnsupportedEncodingException, MessagingException {
		EmailPayload payload = new EmailPayload();
		payload.setTo(emailAddress);
		payload.setSubject(emailSubject);
		payload.setBody(emailMessage);
		payload.setDateTime(ZonedDateTime.now());
		emailServiceImpl.processEmail(payload);		
	}

	@Override
	public Page<Transaction> getRecentTransactions(PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> requested first {} recent transactions", AppConstants.ANONYMOUS, pageRequest.getPageSize());
			return transactionRepository.findRecentTransactions(pageRequest);
		} catch (Exception e) {
			log.info("{}'s ==> request to fetch first {} recent transactions failed", AppConstants.ANONYMOUS, pageRequest.getPageSize());
			throw new Exception("Could not fetch recent transaction records from Database");	
		}
	}

	@Override
	public Page<Transaction> getAllTransactionsBySearchParams(String transactionSearchParam, PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> searching for transaction matching '{}'", AppConstants.ANONYMOUS, transactionSearchParam);
			return transactionRepository.findTransactionsMatching(transactionSearchParam, pageRequest);
		} catch (Exception e) {
			log.info("{}'s ==> search for transaction matching '{}' failed", AppConstants.ANONYMOUS, transactionSearchParam);
			throw new Exception("Could not fetch transaction records matching ["+ transactionSearchParam + "]");	
		}
	}

}
