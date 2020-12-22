package com.example.simplerestapis.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class UserOrgPK implements Serializable {
	public UserOrgPK()
	{
		super();
	}
	public UserOrgPK(String userId, SalesforceOrg sforg) {
		super();
		this.userId = userId;
		this.sforg = sforg;
	}
	
	@Column(name="user_id")
	private String userId;
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="sforg_id")
	private SalesforceOrg sforg;
	
	

	public SalesforceOrg getSforg() {
		return sforg;
	}

	public void setSforg(SalesforceOrg sforg) {
		this.sforg = sforg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
