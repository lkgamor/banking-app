package com.louisga.banking.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.louisga.banking.model.CardType;
import com.louisga.banking.service.CardService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;

	@GetMapping("/cards")
	String getCardsPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "cardTypeName") String sort) throws Exception {
		Page<CardType> cardList = cardService.getPagedCards(PageRequest.of(page, 10, Direction.ASC, sort));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_CARDS, cardList);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, cardList.getTotalElements());
		return "pages/cards/index";
	}

}
