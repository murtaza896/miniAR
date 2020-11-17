package com.example.simplerestapis.models;

import java.util.Date;

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
	@Column(name="organization_id")
	String organizationId;
	
	@Column(name="access_token")
	String accessToken;
	
	@Column(name="refresh_token")
	String refreshToken;
	
	@Column(name="client_id")
	String clientId;
	
	@Column(name="client_secret")
	String clientSecret;
	
	@Column(name="identity_url")
	String identityUrl;
	
	@Column(name="instance_url")
	String instanceUrl;
	
	@Column(name="issued_at")
	String issuedAt;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	public SalesforceOrg() {
		
	}
	
	public SalesforceOrg(String organizationId, String accessToken, String refreshToken, String clientId,
			String clientSecret, String identityUrl, String instanceUrl, String issuedAt, User user) {
		super();
		this.organizationId = organizationId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.identityUrl = identityUrl;
		this.instanceUrl = instanceUrl;
		this.issuedAt = issuedAt;
		this.user = user;
	}

	public SalesforceOrg(String organizationId, String accessToken, String refreshToken, String clientId,
			String clientSecret, String identityUrl, String instanceUrl, String issuedAt) {
		super();
		this.organizationId = organizationId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.identityUrl = identityUrl;
		this.instanceUrl = instanceUrl;
		this.issuedAt = issuedAt;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
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
	
	
}
