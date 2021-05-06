package com.louisga.banking.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class TransactionAudit implements Serializable{

	private static final long serialVersionUID = -1111212672795500070L;	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name= "audit_id")
	private String id;
	
	@Column(name= "transaction_id")
	private String transactionId;
	
	@Column(name= "transaction_type")
	private String transactionType;
	
	@Column(name= "date_edited")
	private String dateEdited;
	
	@Column(name= "transaction_amount")
	private Double transactionAmount;
	
	@Column(name= "total_balance")
	private Double totalBalance;
	
	@Column(name= "transaction_account_id")
	private String transactionAccountID;
	
	@Column(name= "transaction_account_name")
	private String transactionAccountName;

}
