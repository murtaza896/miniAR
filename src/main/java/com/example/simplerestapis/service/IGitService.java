package com.example.simplerestapis.service;



import java.util.List;
import java.util.Optional;

import com.example.simplerestapis.models.Git;

public interface IGitService {

	List<Git> getGitsByUser(String user);

	Optional<Git> getGitById(long id);

	void updateGit(Git git);

	void addGit(String username, String password, String repoURL, String message, boolean isDone);

	void deleteGit(long id);
	
	void saveGit(Git git);

	

}