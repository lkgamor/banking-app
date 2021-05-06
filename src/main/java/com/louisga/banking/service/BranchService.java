package com.louisga.banking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.louisga.banking.model.Branch;

import javassist.NotFoundException;

public interface BranchService {

	List<Branch> getBranches() throws Exception;

	List<Branch> getActiveBranches() throws Exception;

	Branch saveBranch(Branch branch) throws Exception;

	Branch getBranchDetails(String id) throws NotFoundException;

	Optional<Branch> getBranchDetailsByName(String branchName) throws NotFoundException;

	Branch updateBranch(String id, Branch newBranch) throws Exception;

	String deleteBranch(String id) throws Exception;

	Page<Branch> getPagedBranches(PageRequest pageRequest) throws Exception;

}