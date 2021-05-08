package com.louisga.banking.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.louisga.banking.model.Role;
import com.louisga.banking.repository.RoleRepository;
import com.louisga.banking.service.RoleService;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImplementation implements RoleService {

	private final RoleRepository roleRepository;

	@Override
	public List<Role> getAllRoles() throws Exception {		
		try {
			log.info("{} ==> requested roles from database", AppConstants.ANONYMOUS);
			return roleRepository.findAll();
		} catch (Exception e) {
			log.error("{}'s ==> request to get roles from database failed", AppConstants.ANONYMOUS);
			throw new Exception("Could not fetch roles from Database");
		}	
	}
}
