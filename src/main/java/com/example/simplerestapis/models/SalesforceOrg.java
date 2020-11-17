package com.example.simplerestapis.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	
	@Column(name="identity_url")
	String identityUrl;
	
	@Column(name="instance_url")
	String instanceUrl;
	
	@Column(name="issued_at")
	String issuedAt;

	public SalesforceOrg(String organizationId, String accessToken, String refreshToken, String identityUrl, String instanceUrl, String issuedAt) {
		super();
		this.organizationId = organizationId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.identityUrl = identityUrl;
		this.instanceUrl = instanceUrl;
		this.issuedAt = issuedAt;
	}

	public SalesforceOrg() {
		
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
