package com.example.simplerestapis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.simplerestapis.models.GitStore;

@Repository
public interface GitStoreRepository extends JpaRepository<GitStore, String>{
	
}
