package com.louisga.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.louisga.banking.model.Branch;

public interface BranchRepository extends JpaRepository<Branch, Integer> {

	Optional<Branch> findByBranchId(String string);

	Optional<Branch> findByBranchName(String branchName);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE Branch b set b.branchName =:branchName, b.branchAddress=:branchAddress WHERE b.id = :id")
	void updateBranch(@Param("branchName") String accountName, @Param("branchAddress") String branchAddress, @Param("id") String id);

	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE Branch b set b.branchState = 'inactive' WHERE b.id = :id")
	void deleteByBranchId(String id);

	@Query(value = "SELECT b FROM Branch b WHERE b.branchState = :state")
	List<Branch> findAll(String state);

	@Query(value = "SELECT b FROM Branch b")
	Page<Branch> findAllPagedBranches(PageRequest pageRequest);
}
