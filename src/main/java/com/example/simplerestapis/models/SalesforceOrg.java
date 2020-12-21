package com.example.simplerestapis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sun.istack.NotNull;

@Entity
@Table(name="sforgs")
public class SalesforceOrg {
	
	@Id
	@NotNull
	@Column(name="id")
	String id;
	
	@Column(name="access_token")
	String accessToken;
	
	@Column(name="refresh_token")
	String refreshToken;
	
	@Column(name="identity_url")
	String identityUrl;
	
	@Column(name="instance_url")
	String instanceUrl;
	
	@Column(name="issued_at")
	String issuedAt;
	
	@Column(name="username")
	String username;
	
	@Column(name = "nick_name")
	String nickName;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

//	@ManyToOne(fetch = FetchType.EAGER, optional = false)
//	@JoinColumn(name = "user_id")
	@Column(name = "user_id")
	String userId;

	public SalesforceOrg() {
		
	}
	
	
	

	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getIdentityUrl() {
		return identityUrl;
	}

	public void setIdentityUrl(String identityUrl) {
		this.identityUrl = identityUrl;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}

	public SalesforceOrg(String id, String accessToken, String refreshToken, String identityUrl, String instanceUrl,
			String issuedAt, String username, String nickName, String userId) {
		super();
		this.id = id;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.identityUrl = identityUrl;
		this.instanceUrl = instanceUrl;
		this.issuedAt = issuedAt;
		this.username = username;
		this.nickName = nickName;
		this.userId = userId;
	}

	public SalesforceOrg(String id, String accessToken, String refreshToken, String identityUrl, String instanceUrl,
			String issuedAt, String username, String nickName) {
		super();
		this.id = id;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.identityUrl = identityUrl;
		this.instanceUrl = instanceUrl;
		this.issuedAt = issuedAt;
		this.username = username;
		this.nickName = nickName;
	}
	
	
}
