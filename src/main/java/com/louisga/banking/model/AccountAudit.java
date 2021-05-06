package com.louisga.banking.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class AccountAudit implements Serializable{
	
	private static final long serialVersionUID = -9096601235433432821L;
	
	@Id
	@Column(name= "account_number")
	private String accountNumber;
	
	@Column(name ="account_name")
	private String accountName;

	@Column(name ="account_contact_number")
	private String accountContactNumber;
	
	@Column(name ="account_email")
	private String accountEmail;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="account_id_type", referencedColumnName = "card_type_id")
	private CardType card;
	
	@Column(name ="account_id_number")
	private String accountIDNumber;
	
	@Column(name ="account_occupation")
	private String accountOccupation;
	
	@Column(name= "account_balance")
	private Double accountBalance;

	@Column(name= "date_edited")
	private String dateEdited;

	@Column(name= "account_action")
	private String accountAction;
		
}
