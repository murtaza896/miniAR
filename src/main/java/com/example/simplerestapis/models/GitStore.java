package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sun.istack.NotNull;

public class GitStore {
	
	String repoId;
	String repoUrl;
	String repoName;
	String username;
	private User user;
	private SalesforceOrg org;
	
	public GitStore(String repoId, String repoUrl, String repoName, String username, User user,
			SalesforceOrg org) {
		super();
		this.repoId = repoId;
		this.repoUrl = repoUrl;
		this.repoName = repoName;
		this.username = username;
		this.user = user;
		this.org = org;
	}
	
	public GitStore(String repoId, String repoUrl, String repoName, String username) {
		super();
		this.repoId = repoId;
		this.repoUrl = repoUrl;
		this.repoName = repoName;
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SalesforceOrg getOrg() {
		return org;
	}

	public void setOrg(SalesforceOrg org) {
		this.org = org;
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
