package com.louisga.banking.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.model.CardType;
import com.louisga.banking.repository.CardTypeRepository;
import com.louisga.banking.service.CardService;
import com.louisga.banking.utility.AppConstants;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CardServiceImplementation implements CardService {
	
	private final CardTypeRepository cardRepository;
	private final CompanyConfiguration companyConfig;
		
	@Override
	public List<CardType> getCards() throws Exception {
		try {
			log.info("{} ==> requested all card types from database", AppConstants.ANONYMOUS);
			return cardRepository.findAll();
		} catch (Exception e) {
			log.error("{}'s ==> request to get all card types from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch cards from Database");
		}	
	}

	@Override
	public CardType saveCard(CardType card) throws Exception {
		
		if(!card.getCardTypeName().isBlank() || !card.getCardTypeName().isEmpty()) {
			UUID cardUUID = UUID.randomUUID();
			
			card.setCardTypeId(cardUUID.toString());
			String cardName = card.getCardTypeName().toUpperCase();
			card.setCardTypeName(cardName);
			try {
				log.info("{} ==> created a card type", AppConstants.ANONYMOUS);
				return cardRepository.save(card);
			} catch (Exception e) {
				log.error("{}'s ==> request to create card type [{}] in database failed", AppConstants.ANONYMOUS, cardName);
				throw new Exception("Save Card [" + cardName + "] Failed");			
			}
		} else {
			throw new Exception("Provide a valid card name");		
		}
	}

	@Override
	public CardType updateCard(String id, CardType newCard) throws Exception {
		Optional<CardType> oldCard = cardRepository.findByCardTypeId(id);
		if(oldCard.isPresent()) {
			try {
				log.info("{} ==> updated details of branch '{}' in database", AppConstants.ANONYMOUS, oldCard.get().getCardTypeName());
				return cardRepository.save(newCard);
			} catch (Exception e) {
				log.error("{}'s ==> request to update details card type '{}' in database failed", AppConstants.ANONYMOUS, oldCard.get().getCardTypeName());
				throw new Exception("Update Card [" + oldCard.get().getCardTypeName() + "] Failed");			
			}		
		} else {
			throw new NotFoundException("Card with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}				
	}

	@Override
	public String deleteCard(String id) throws Exception {
		Optional<CardType> card = cardRepository.findByCardTypeId(id);
		if(card.isPresent()) {
			try {
				cardRepository.deleteByCardTypeId(id);
				log.info("{} ==> deleted card type '{}' from database", AppConstants.ANONYMOUS, card.get().getCardTypeName());
				return id;
			} catch (Exception e) {
				log.error("{}'s ==> request to delete details card type '{}' from database failed", AppConstants.ANONYMOUS, card.get().getCardTypeName());
				throw new Exception("Delete Card [" + card.get().getCardTypeName() + "] Failed");			
			}			
		} else {
			throw new NotFoundException("Card with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}

	@Override
	public CardType getCardDetails(String id) throws NotFoundException {
		Optional<CardType> card = cardRepository.findByCardTypeId(id);
		if(card.isPresent()) {
			log.info("{} ==> requested details of card type '{}' from database", AppConstants.ANONYMOUS, card.get().getCardTypeName());
			return card.get();
		} else {
			log.error("{}'s ==> request to get details card type '{}' from database failed", AppConstants.ANONYMOUS, card.get().getCardTypeName());
			throw new NotFoundException("Card with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}

	@Override
	public Page<CardType> getPagedCards(PageRequest pageRequest) throws Exception {
		try {
			log.info("{} ==> requested a page of all cards from database", AppConstants.ANONYMOUS);
			return cardRepository.findAllPagedCards(pageRequest);
		} catch (NoResultException e) {
			log.error("{}'s ==> request to get a page of all cards from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch cards from Database");
		}	
	}
}
