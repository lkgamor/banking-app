package com.louisga.banking.repository;


import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.louisga.banking.model.LoanPayment;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {

	ArrayList<LoanPayment> findByLoanIdOrderByLoanPaymentDateAsc(String loanId);
}
