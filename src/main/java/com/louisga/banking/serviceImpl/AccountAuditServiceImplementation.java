package com.louisga.banking.serviceImpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.louisga.banking.model.AccountAudit;
import com.louisga.banking.repository.AccountAuditRepository;
import com.louisga.banking.service.AccountAuditService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountAuditServiceImplementation implements AccountAuditService {

	private final AccountAuditRepository accountAuditRepository;
	
	@Override
	public List<AccountAudit> getAllAccountAudits() throws Exception {		
		try {
			log.info("{} ==> requested all account audits from database", AppConstants.ANONYMOUS);
			return accountAuditRepository.findAll();
		} catch (Exception e) {
			log.error("{}'s ==> request to get all account audits from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch account audits from Database");
		}
	}

	@Override
	public Page<AccountAudit> getPagedAccountAudits(PageRequest pageRequest) throws Exception {		
		try {
			log.info("{} ==> requested a page of all account audits from database", AppConstants.ANONYMOUS);
			return accountAuditRepository.findAllAccountAudits(pageRequest);
		} catch (Exception e) {
			log.error("{}'s ==> request to get a page of all account audits from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch account audits from Database");
		}
	}

}
