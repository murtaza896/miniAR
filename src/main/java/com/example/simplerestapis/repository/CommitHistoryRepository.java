package com.example.simplerestapis.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simplerestapis.models.CommitHistory;

public interface CommitHistoryRepository extends JpaRepository<CommitHistory, Integer> {

	ArrayList<CommitHistory> findByUserId(String userId);
	
}
