package com.example.simplerestapis.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simplerestapis.models.UserGitAcc;
import com.example.simplerestapis.models.UserGitAccPK;

public interface UserGitAccRepository extends JpaRepository<UserGitAcc, UserGitAccPK> {

	ArrayList<UserGitAcc> findByUserId(String user_id);
	
}