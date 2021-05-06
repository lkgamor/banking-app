package com.louisga.banking.restcontroller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.louisga.banking.model.CardType;
import com.louisga.banking.service.CardService;

import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cards")
public class CardRestController {
	
	private final CardService cardService;
	
	/**
	 * GET api/v1/cards retrieve all card types
	 * @return ResponseEntity<List<CardType>>
	 * @throws Exception 
	 */
	@GetMapping()
	@ApiOperation(value = "Finds all supported ID Card types.",
	  notes = "This API returns all the types of ID Cards accepted at this company.")
	public @ResponseBody ResponseEntity<List<CardType>> getAllCards() throws Exception{
		return new ResponseEntity<>(cardService.getCards(), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/cards/:id retrieve a card
	 * @param id
	 * @return ResponseEntity<CardType>
	 * @throws NotFoundException 
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "Find an ID Card type.",
	  notes = "Provide an id to look up a specific ID Card accepted at this company.",
	  response = CardType.class)
	public @ResponseBody ResponseEntity<CardType> getCardType(@PathVariable String id) throws NotFoundException {
		return new ResponseEntity<>(cardService.getCardDetails(id), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/cards create a card type
	 * @param card
	 * @return ResponseEntity<?>
	 * @throws Exception 
	 */
	@PostMapping()
	@GetMapping("/{id}")
	@ApiOperation(value = "Create an ID Card type.",
	  notes = "Provide a payload to create a new ID Card that this company accepts.",
	  response = CardType.class)
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public @ResponseBody ResponseEntity<CardType> saveCard(@RequestBody final CardType card) throws Exception {		
		return new ResponseEntity<>(cardService.saveCard(card), HttpStatus.CREATED);
	}
	
	/**
	 * GET api/v1/cards/:id update a card
	 * @param id
	 * @param card
	 * @return ResponseEntity<?>
	 * @throws Exception 
	 */
	@PutMapping("/{id}")
	@ApiOperation(value = "Update an ID Card type.",
	  notes = "Provide an id and a payload to look up and update the details of a specific ID Card type accepted by this company.",
	  response = CardType.class)
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public @ResponseBody ResponseEntity<CardType> updateCard(@PathVariable String id, @RequestBody final CardType card) throws Exception{
		return new ResponseEntity<>(cardService.updateCard(id, card), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/cards/:id delete a card
	 * @param id
	 * @return ResponseEntity<?>
	 * @throws Exception 
	 */
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Delete an ID Card type.",
	  notes = "Provide an id to remove the details of a specific ID Card type accepted by this company.",
	  response = String.class)
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public @ResponseBody ResponseEntity<String> deleteCard(@PathVariable String id) throws Exception{		
		return new ResponseEntity<>(cardService.deleteCard(id), HttpStatus.ACCEPTED);
	}
	
}
