package com.example.simplerestapis.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simplerestapis.models.UserOrgPK;
import com.example.simplerestapis.models.UserOrgs;

public interface UserOrgsRepository extends JpaRepository<UserOrgs, UserOrgPK> {

	ArrayList<UserOrgs> findByUserId(String user_id);
	
}