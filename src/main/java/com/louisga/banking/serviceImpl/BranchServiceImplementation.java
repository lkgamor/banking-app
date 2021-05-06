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
import com.louisga.banking.model.Branch;
import com.louisga.banking.repository.BranchRepository;
import com.louisga.banking.service.BranchService;
import com.louisga.banking.utility.AppConstants;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BranchServiceImplementation implements BranchService {
	
	private final BranchRepository branchRepository;
	private final CompanyConfiguration companyConfig;
		
	@Override
	public List<Branch> getBranches() throws Exception {		
		try {
			log.info("{} ==> requested all branches from database", AppConstants.ANONYMOUS);
			return branchRepository.findAll();
		} catch (Exception e) {
			log.error("{}'s ==> request to get all branches from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch branches from Database");
		}	
	}
	
	@Override
	public List<Branch> getActiveBranches() throws Exception {		
		try {
			log.info("{} ==> requested active branches from database", AppConstants.ANONYMOUS);
			return branchRepository.findAll("active");
		} catch (Exception e) {
			log.error("{}'s ==> request to get active branches from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch active branches from Database");

		}	
	}

	@Override
	public Branch saveBranch(Branch branch) throws Exception {
		
		UUID branchUUID = UUID.randomUUID();
		String branchName = branch.getBranchName().toUpperCase();
		String branchAddress = branch.getBranchAddress().toUpperCase();
		
		if(!branchName.isBlank() || !branchName.isEmpty()) {
			
			if(!branchAddress.isBlank() || !branchAddress.isEmpty()) {

				branch.setBranchId(branchUUID.toString());
				branch.setBranchName(branchName);
				branch.setBranchAddress(branchAddress);
				branch.setBranchState("active");
				try {
					log.info("{} ==> created a branch", AppConstants.ANONYMOUS);
					return branchRepository.save(branch);
				} catch (Exception e) {
					log.error("{}'s ==> request to create branch '{}' in database failed", AppConstants.ANONYMOUS, branchName);
					throw new Exception("Save Branch [" + branchName + "] Failed");	
				}
				
			} else {
				throw new NotFoundException("Branch address cannot be empty");
			}
			
		} else {
			throw new NotFoundException("Branch name cannot be empty");
		}		
	}

	@Override
	public Branch getBranchDetails(String id) throws NotFoundException {		
		Optional<Branch> branch = branchRepository.findByBranchId(id);
	    if(branch.isPresent()) {
	    	log.info("{} ==> requested details of branch '{}' from database", AppConstants.ANONYMOUS, branch.get().getBranchName());
	    	return branch.get();
		} else {
	    	log.info("{}'s ==> request to get details of branch '{}' from database failed", AppConstants.ANONYMOUS, branch.get().getBranchName());
			throw new NotFoundException("Branch with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Override
	public Optional<Branch> getBranchDetailsByName(String branchName) throws NotFoundException {		
		Optional<Branch> branch = branchRepository.findByBranchName(branchName);
	    if(branch.isPresent()) {
	    	log.info("{} ==> requested details of branch '{}' from database", AppConstants.ANONYMOUS, branch.get().getBranchName());
	    	return branch;
		} else {
	    	log.info("{}'s ==> request to get details of branch from database failed", AppConstants.ANONYMOUS);
			throw new NotFoundException("Branch with name ["+ branchName +"] does not exist with ".concat(companyConfig.getName()));
		}	
	}
	
	@Override
	public Branch updateBranch(String id, Branch newBranch) throws Exception {
		Optional<Branch> oldBranch = branchRepository.findByBranchId(id);
		if(oldBranch.isPresent()) {
			
			String newBranchName = newBranch.getBranchName().toUpperCase();
			String newBranchAddress = newBranch.getBranchAddress().toUpperCase();
			
			if(!newBranchName.isBlank() || !newBranchName.isEmpty()) {
				
				if(!newBranchAddress.isBlank() || !newBranchAddress.isEmpty()) {

					try {
						log.info("{} ==> updated details of branch '{}' in database", AppConstants.ANONYMOUS, oldBranch.get().getBranchName());
						newBranch.setBranchId(id);
						newBranch.setBranchState("active");
						return branchRepository.save(newBranch);
					} catch (Exception e) {
						log.error("{}'s ==> request to update details of branch '{}' in database failed", AppConstants.ANONYMOUS, oldBranch.get().getBranchName());
						throw new Exception("Update Branch [" + oldBranch.get().getBranchName() + "] Failed");
					}
					
				} else {
					throw new NotFoundException("New branch address cannot be empty");
				}
				
			} else {
				throw new NotFoundException("New branch name cannot be empty");
			}	
		} else {
			throw new NotFoundException("Branch with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}				
	}

	@Override
	public String deleteBranch(String id) throws Exception {
		Optional<Branch> branch = branchRepository.findByBranchId(id);
		if(branch.isPresent()) {
			try {
				branchRepository.deleteByBranchId(id);
				log.info("{} ==> deleted details of branch '{}' from database", AppConstants.ANONYMOUS, branch.get().getBranchName());
				return id;
			} catch (Exception e) {
				log.error("{}'s ==> request to delete details of branch '{}' from database failed", AppConstants.ANONYMOUS, branch.get().getBranchName());
				throw new Exception("Delete Branch [" + branch.get().getBranchName() + "] Failed");	
			}	
		} else {
			throw new NotFoundException("Branch with id ["+ id +"] does not exist with ".concat(companyConfig.getName()));
		}				
	}

	@Override
	public Page<Branch> getPagedBranches(PageRequest pageRequest) throws Exception {		
		try {
			log.info("{} ==> requested a page of all branches from database", AppConstants.ANONYMOUS);
			return branchRepository.findAllPagedBranches(pageRequest);
		} catch (NoResultException e) {
			log.error("{}'s ==> request to get a page of all branches from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch branches from Database");
		}		
	}
}
