package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="user_gitacc")
public class UserGitAcc {
	
	public UserGitAcc() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserGitAcc(UserGitAccPK userGitAccPK, String userId) {
		super();
		this.userGitAccPK = userGitAccPK;
		this.userId = userId;
	}

	@EmbeddedId
    private UserGitAccPK userGitAccPK;

	@Column(name="user_id", insertable=false, updatable=false)
	private String userId;
	
	public UserGitAccPK getUserGitAccPK() {
		return userGitAccPK;
	}

	public void setUserGitAccPK(UserGitAccPK userGitAccPK) {
		this.userGitAccPK = userGitAccPK;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
