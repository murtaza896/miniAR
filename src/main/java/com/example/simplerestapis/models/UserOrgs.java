package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="user_orgs")
public class UserOrgs {
	
	public UserOrgs() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserOrgs(UserOrgPK userOrgPK, String userId) {
		super();
		this.userOrgPK = userOrgPK;
		this.userId = userId;
	}

	@EmbeddedId
    private UserOrgPK userOrgPK;

	@Column(name="user_id", insertable=false, updatable=false)
	private String userId;
	
	public UserOrgPK getUserOrgPK() {
		return userOrgPK;
	}

	public void setUserOrgPK(UserOrgPK userOrgPK) {
		this.userOrgPK = userOrgPK;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
