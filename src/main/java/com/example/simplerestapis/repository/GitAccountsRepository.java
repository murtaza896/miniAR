package com.example.simplerestapis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simplerestapis.models.GitAccounts;

public interface GitAccountsRepository extends JpaRepository<GitAccounts, Integer> {
	
}
