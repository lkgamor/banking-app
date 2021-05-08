package com.louisga.banking.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;


@Entity
@Data
public class LoanPayment implements Serializable {

	private static final long serialVersionUID = 5640938201158286827L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private String id;
	
	@Column(name="loan_id")
	private String loanId;

	@Column(name="loan_payment_date")
	private ZonedDateTime loanPaymentDate;

	@Column(name="loan_payment_amount")
	private Double loanPaymentAmount;
	
	@Column(name="loan_payment_balance")
	private Double loanPaymentBalance;

	@Column(name="loan_status")
	private Boolean loanStatus;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name= "loan_payment_issued_by", referencedColumnName = "employee_id")
	private Employee employee;
	
}
