package com.louisga.banking.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Account implements Serializable {

	private static final long serialVersionUID = 2549466338773211133L;
	
	@Id
	@Column(name= "account_number")
	private String accountNumber;
	
	@Column(name= "account_name")
	private String accountName;
	
	@Column(name= "account_contact_number")
	private String accountContact;

	@Column(name= "account_kin_name")
	private String accountKinName;

	@Column(name= "account_kin_contact")
	private String accountKinContact;
	
	@Column(name= "account_email")
	private String accountEmail;
	
	@Column(name= "account_date_opened")
	private ZonedDateTime accountDateOpened;

	@Column(name= "account_date_closed")
	private ZonedDateTime accountDateClosed;
	
	@Column(name= "account_user_dob")
	private String accountUserDOB;

	@Column(name= "account_balance")
	private Double accountBalance;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="account_id_type", referencedColumnName = "card_type_id")
	private CardType card;

	@Column(name= "account_id_number")
	private String accountIDNumber;

	@Column(name= "account_occupation")
	private String accountOccupation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="account_branch", referencedColumnName = "branch_id")
	private Branch branch;
	
	@Column(name= "account_user_gender")
	private String accountUserGender;
	
	@Column(name= "loaned")
	private Boolean accountOwing;

	@Column(name= "account_status")
	private Boolean accountStatus;
	
	@Column(name= "account_signature")
	private byte[] accountSignature;
	
	@Column(name= "account_image")
	private byte[] accountImage;
}
