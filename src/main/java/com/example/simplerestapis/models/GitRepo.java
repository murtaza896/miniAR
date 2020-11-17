package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.eclipse.jgit.annotations.NonNull;


@Entity
@Table(name="gitrepos")
public class GitRepo {
	
	@Id
	@NonNull
	@Column(name = "repo_id")
	String repo_id;
	
	@Column(name = "repo_name")
	String repo_name;
	
	@Column(name = "repo_url")
	String repo_url;
	
	@Column(name = "username")
	String username;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "org_id")
	private SalesforceOrg org;

	public String getRepo_id() {
		return repo_id;
	}

	public void setRepo_id(String repo_id) {
		this.repo_id = repo_id;
	}

	public String getRepo_name() {
		return repo_name;
	}

	public void setRepo_name(String repo_name) {
		this.repo_name = repo_name;
	}

	public String getRepo_url() {
		return repo_url;
	}

	public void setRepo_url(String repo_url) {
		this.repo_url = repo_url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
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

	public GitRepo(String repo_id, String repo_name, String repo_url, String username, User user,
			SalesforceOrg org) {
		super();
		this.repo_id = repo_id;
		this.repo_name = repo_name;
		this.repo_url = repo_url;
		this.username = username;
		this.user = user;
		this.org = org;
	}
	
	public GitRepo(String repo_id, String repo_name, String repo_url, String username) {
		super();
		this.repo_id = repo_id;
		this.repo_name = repo_name;
		this.repo_url = repo_url;
		this.username = username;
//		this.user = user;
//		this.org = org;
	}

	public GitRepo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
