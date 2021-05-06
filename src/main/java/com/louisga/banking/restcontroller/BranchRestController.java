package com.louisga.banking.restcontroller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.louisga.banking.model.Branch;
import com.louisga.banking.service.BranchService;

import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/branches")
public class BranchRestController {
	
	private final BranchService branchService;
	
	/**
	 * GET api/v1/branches retrieve all branches
	 * @return ResponseEntity<List<Branch>>
	 * @throws Exception 
	 */
	@GetMapping()
	@ApiOperation(value = "Finds all branches",
	notes = "This API returns all the branches of this company. Including inactive and active branches")
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public ResponseEntity<List<Branch>> getAllBranches() throws Exception{
		return new ResponseEntity<>(branchService.getBranches(), HttpStatus.ACCEPTED);
	}
	
	/**
	 * GET api/v1/branches/active retrieve all branches
	 * @return ResponseEntity<List<Branch>>
	 * @throws Exception 
	 */
	@GetMapping("/active")
	@ApiOperation(value = "Finds all active branches",
	  notes = "This API returns only branches of this company that are operational")
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public ResponseEntity<List<Branch>> getAllActiveBranches() throws Exception{
		return new ResponseEntity<>(branchService.getActiveBranches(), HttpStatus.ACCEPTED);
	}
	
	/**
	 * GET api/v1/branches/:id retrieve a branch
	 * @param id
	 * @return ResponseEntity<Branch>
	 * @throws NotFoundException 
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "Finds a branch",
	  notes = "Provide an id to look up a specific branch of this company. It must be an active branch",
	  response = Branch.class)
	public ResponseEntity<Branch> getBranch(@PathVariable String id) throws NotFoundException{
		return new ResponseEntity<>(branchService.getBranchDetails(id), HttpStatus.ACCEPTED);
	}
	
	/**
	 * GET api/v1/branches create a branch
	 * @param branch
	 * @return ResponseEntity<?>
	 * @throws Exception 
	 */
	@PostMapping()
	@ApiOperation(value = "Creates a branch",
	  notes = "Provide a payload to create a branch for this company.",
	  response = Branch.class)
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public ResponseEntity<Branch> saveBranch(@RequestBody final Branch branch) throws Exception {
		return new ResponseEntity<>(branchService.saveBranch(branch), HttpStatus.CREATED);
	}
	
	/**
	 * GET api/v1/branches/:id update a branch
	 * @param id
	 * @param branch
	 * @return ResponseEntity<?>
	 * @throws Exception 
	 */
	@PutMapping("/{id}")
	@ApiOperation(value = "Updates a branch",
	  notes = "Provide an id and a payload to look up and update the details of a specific branch of this company.",
	  response = Branch.class)
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public ResponseEntity<Branch> updateBranch(@PathVariable String id, @RequestBody final Branch branch) throws Exception{
		return new ResponseEntity<>(branchService.updateBranch(id, branch), HttpStatus.OK);
	}
	
	/**
	 * GET api/v1/branches/:id delete a branch
	 * @param id
	 * @return ResponseEntity<?>
	 * @throws Exception 
	 */
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Close a branch",
	  notes = "Provide an id to close down a specific branch of this company. It will be made inactive.",
	  response = String.class)
	//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public ResponseEntity<String> deleteBranch(@PathVariable String id) throws Exception{		
		return new ResponseEntity<>(branchService.deleteBranch(id), HttpStatus.ACCEPTED);
	}

}
