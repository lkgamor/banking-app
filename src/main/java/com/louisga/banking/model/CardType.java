package com.louisga.banking.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class CardType {

	@Id
	@Column(name="card_type_id")
	private String cardTypeId;
	
	@Column(name="card_type_name")
	private String cardTypeName;	
	
}
