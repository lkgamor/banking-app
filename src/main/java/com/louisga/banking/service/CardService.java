package com.louisga.banking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.CardType;

import javassist.NotFoundException;

public interface CardService {

	List<CardType> getCards() throws Exception;

	CardType saveCard(CardType card) throws Exception;

	CardType updateCard(String id, CardType newCard) throws Exception;

	String deleteCard(String id) throws Exception;

	CardType getCardDetails(String id) throws NotFoundException;

	Page<CardType> getPagedCards(PageRequest pageRequest) throws Exception;

}