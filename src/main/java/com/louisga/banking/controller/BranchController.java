package com.louisga.banking.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.louisga.banking.model.Branch;
import com.louisga.banking.service.BranchService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BranchController {

	private final BranchService branchService;	

	@GetMapping("/branches")
	String getBranchesPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "branchName") String sort) throws Exception {
		Page<Branch> branchList = branchService.getPagedBranches(PageRequest.of(page, 10, Direction.ASC, sort));
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_BRANCHES, branchList);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_PAGE_COUNT, branchList.getTotalElements());
		return "pages/branches/index";
	}
}
