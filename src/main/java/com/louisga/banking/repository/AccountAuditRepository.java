package com.louisga.banking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.louisga.banking.model.AccountAudit;

public interface AccountAuditRepository extends JpaRepository<AccountAudit, Integer> {

	@Query(value = "SELECT aa FROM AccountAudit aa")
	Page<AccountAudit> findAllAccountAudits(PageRequest pageRequest);
	
}
