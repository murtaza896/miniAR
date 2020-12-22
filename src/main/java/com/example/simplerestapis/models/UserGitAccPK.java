package com.example.simplerestapis.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class UserGitAccPK implements Serializable {
	public UserGitAccPK()
	{
		super();
	}
	public UserGitAccPK(String userId, GitAccounts gitacc) {
		super();
		this.userId = userId;
		this.gitacc = gitacc;
	}
	
	@Column(name="user_id")
	private String userId;
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="gitacc_id")
	private GitAccounts gitacc;

	public GitAccounts getGitacc() {
		return gitacc;
	}
	public void setGitacc(GitAccounts gitacc) {
		this.gitacc = gitacc;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
