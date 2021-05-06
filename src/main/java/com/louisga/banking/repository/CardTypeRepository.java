package com.louisga.banking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.louisga.banking.model.CardType;

public interface CardTypeRepository extends JpaRepository<CardType, Integer> {
	
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE CardType c set c.cardTypeName =:cardTypeName WHERE c.id = :id")
	CardType updateCard(@Param("cardTypeName") String accountName, @Param("id") String id);

	Optional<CardType> findByCardTypeId(String string);

	@Modifying(clearAutomatically = true)
	void deleteByCardTypeId(String id);

	@Query(value = "SELECT c FROM CardType c")
	Page<CardType> findAllPagedCards(PageRequest pageRequest);
}
