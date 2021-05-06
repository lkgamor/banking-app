package com.louisga.banking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.TransactionAudit;

public interface TransactionAuditService {

	List<TransactionAudit> getAllTransactionAudits();

	Page<TransactionAudit> getPagedTransactionAudits(PageRequest pageRequest);

	TransactionAudit getTransactionAuditDetails(String id) throws Exception;

	void deleteTransactionAudit(String id) throws Exception;

}