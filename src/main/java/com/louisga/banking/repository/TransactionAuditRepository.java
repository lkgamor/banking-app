package com.louisga.banking.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.louisga.banking.model.Transaction;
import com.louisga.banking.model.TransactionAudit;

public interface TransactionAuditRepository extends JpaRepository<TransactionAudit, Integer> {

	void save(Transaction transaction);

	@Query(value = "SELECT ta FROM TransactionAudit ta")
	Page<TransactionAudit> findAllTransactionAudits(PageRequest pageRequest);

	Optional<TransactionAudit> findByTransactionId(String id);

	@Modifying(clearAutomatically = true)
	void deleteByTransactionId(String id);

}
