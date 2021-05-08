package com.louisga.banking.model;

import java.time.ZonedDateTime;

import lombok.Data;


@Data
public class LoanToPay {
	
	private String loanAccountName;

	private String loanAccountNumber;
	
	private Double loanAmount;

	private Double loanAmountToPay;
	
	public static LoanPayment toLoanPaymentFromLoanToPay(String loanId, ZonedDateTime loanPaymentDate, Double transactionAmount, Double amountLeftToBePayed, Employee paymentIssuedBy, boolean status) {
		LoanPayment loanPayment = new LoanPayment();
		loanPayment.setLoanId(loanId);
		loanPayment.setLoanPaymentAmount(transactionAmount);
		loanPayment.setLoanPaymentBalance(amountLeftToBePayed);
		loanPayment.setLoanPaymentDate(loanPaymentDate);
		loanPayment.setLoanStatus(status);
		loanPayment.setEmployee(paymentIssuedBy);
		return loanPayment;		
	}

}
