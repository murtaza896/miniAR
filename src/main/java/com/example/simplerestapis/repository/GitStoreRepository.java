package com.example.simplerestapis.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.simplerestapis.models.Git;
import com.example.simplerestapis.models.GitStore;

@Repository
public interface GitStoreRepository extends JpaRepository<GitStore, String>{
	ArrayList<GitStore> findByuser_id(int user_id);
	ArrayList<GitStore> findByaccount_id(int account_id);
	ArrayList<GitStore> findByorg_id(String org_id);
	//List<Git> findByUsername(String user);
}
