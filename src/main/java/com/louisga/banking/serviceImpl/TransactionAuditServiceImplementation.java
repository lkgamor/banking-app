package com.louisga.banking.serviceImpl;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.model.TransactionAudit;
import com.louisga.banking.repository.TransactionAuditRepository;
import com.louisga.banking.service.TransactionAuditService;
import com.louisga.banking.utility.AppConstants;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionAuditServiceImplementation implements TransactionAuditService {

	private final TransactionAuditRepository transactionAuditRepository;
	private final CompanyConfiguration companyConfig;
	
	@Override
	public List<TransactionAudit> getAllTransactionAudits() {		
		try {
			log.info("{} ==> requested all transaction audits from database", AppConstants.ANONYMOUS);
			return transactionAuditRepository.findAll();
		} catch (NoResultException e) {
			throw new NoResultException("Could not fetch transaction audits from Database");
		}	
	}

	@Override
	public Page<TransactionAudit> getPagedTransactionAudits(PageRequest pageRequest) {
		try {
			log.info("{} ==> requested a page of all transaction audits from database", AppConstants.ANONYMOUS);
			return transactionAuditRepository.findAllTransactionAudits(pageRequest);
		} catch (NoResultException e) {
			throw new NoResultException("Could not fetch transaction audits from Database");
		}
	}
	
	@Override
	public TransactionAudit getTransactionAuditDetails(String id) throws Exception {		
		Optional<TransactionAudit> transactionAudit = transactionAuditRepository.findByTransactionId(id);
		if(transactionAudit.isPresent()) {
			try {
				log.info("{} ==> requested details of transaction audit '{}' from database", AppConstants.ANONYMOUS, id);
		    	return transactionAudit.get();
			} catch (Exception e) {
				log.info("{}'s ==> request to get details of transaction audit '{}' from database failed", AppConstants.ANONYMOUS, id);
				throw new Exception("Fetch Transaction Audit [" + transactionAudit.get().getId() + "] for [" + transactionAudit.get().getTransactionAccountName() + "]Failed");
			}
		} else {
			throw new NotFoundException("Transaction Audit with id ["+ id +"] does not have any loans with ".concat(companyConfig.getName()));
		}
	}
		
	@Override
	public void deleteTransactionAudit(String id) throws Exception {
		Optional<TransactionAudit> transactionAudit = transactionAuditRepository.findByTransactionId(id);
		if(transactionAudit.isPresent()) {
			String transactionAuditId = transactionAudit.get().getId();
			try {
				log.info("{} ==> delete transaction audit '{}'", AppConstants.ANONYMOUS, transactionAuditId);
				transactionAuditRepository.deleteByTransactionId(id);
			} catch (Exception e) {
				log.info("{}'s ==> request to delete transaction audit '{}' failed", AppConstants.ANONYMOUS, transactionAuditId);
				throw new Exception("Delete Transaction audit [" + id + "] Failed");
			}
		} else {
			throw new NotFoundException("Transaction Audit with id ["+ id +"] does not have any loans with ".concat(companyConfig.getName()));
		}
	}
}
