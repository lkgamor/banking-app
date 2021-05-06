package com.louisga.banking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.AccountAudit;

public interface AccountAuditService {

	List<AccountAudit> getAllAccountAudits() throws Exception;

	Page<AccountAudit> getPagedAccountAudits(PageRequest pageRequest) throws Exception;

}