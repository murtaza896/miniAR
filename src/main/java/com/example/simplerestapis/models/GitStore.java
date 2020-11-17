package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.NotNull;

@Entity
@Table(name="gitrepos")
public class GitStore {
	
	@Id
	@NotNull
	@Column(name="repo_id")
	String repoId;
	
	@Column(name="access_token")
	String accessToken;
	
	@Column(name="repo_url")
	String repoUrl;
	
	@Column(name="repo_name")
	String repoName;

	@Column 
	String username;
	
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public GitStore(String repoId, String accessToken, String repoUrl, String username, String repoName) {
		super();
		this.repoId = repoId;
		this.accessToken = accessToken;
		this.repoUrl = repoUrl;
		this.username = username;
		this.repoName = repoName;
	}
	
	
	public  GitStore() {
		
	}
	
	
}
