package com.example.simplerestapis.models;

public class GitStore {
	
	String repoId;
	String repoUrl;
	String repoName;
	String username;
	int userId;


	public GitStore(String repoId, String repoUrl, String repoName, String username, int userId) {
		super();
		this.repoId = repoId;
		this.repoUrl = repoUrl;
		this.repoName = repoName;
		this.username = username;
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRepoId() {
		return repoId;
	}

	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}


	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}
	
	
	public  GitStore() {
		
	}
	
	
}
