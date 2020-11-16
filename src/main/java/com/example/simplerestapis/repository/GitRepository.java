package com.example.simplerestapis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simplerestapis.models.Git;

public interface GitRepository extends JpaRepository<Git, Long>{
	List<Git> findByUsername(String username);
}
