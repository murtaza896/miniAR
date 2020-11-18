package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="git_accounts")
public class GitAccounts {
	
	@Id
	@Column(name = "accountId")
	int accountId;
	
	@Column(name = "username")
	String username;
	
	@Column(name = "avatar_url")
	String avatar_url;
	
	@Column(name = "access_token")
	String access_token;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id")
	private User user;


	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GitAccounts(int account_id, String username, String avatar_url, String access_token, User user) {
		super();
		this.accountId = account_id;
		this.username = username;
		this.avatar_url = avatar_url;
		this.access_token = access_token;
		this.user = user;
	}

	public GitAccounts() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
