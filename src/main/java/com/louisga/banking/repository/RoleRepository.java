package com.louisga.banking.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.louisga.banking.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
