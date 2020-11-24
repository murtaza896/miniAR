package com.example.simplerestapis.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplerestapis.models.CommitHistory;
import com.example.simplerestapis.repository.CommitHistoryRepository;

@Service
public class CommitHistoryService {
	@Autowired
	private CommitHistoryRepository repository;
	
	public CommitHistory getUserById(int id){
		return repository.findById(id).orElse(null);
	}
	
	public ArrayList<CommitHistory> listCommitHistory(int userId){
		return repository.findByuser_id(userId);
	}

}
