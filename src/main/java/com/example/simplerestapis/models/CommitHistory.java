package com.example.simplerestapis.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sun.istack.NotNull;

@Entity
@Table(name="commit_history")
public class CommitHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@NotNull
	int id;
	
	@Column(name="commit_hash")
	String commit_hash;
	
	@Column(name="repo_url")
	String repo_url;
	
	@Column(name="timestamp")
	Timestamp timestamp;
	
	@ManyToOne(fetch = FetchType.EAGER,optional= false)
	@JoinColumn(name = "user_id")
	private User user;
	
	
	
	@ManyToOne(fetch = FetchType.EAGER,optional= false)
	@JoinColumn(name = "sforg_id")
	private SalesforceOrg sforg;



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getCommit_hash() {
		return commit_hash;
	}



	public void setCommit_hash(String commit_hash) {
		this.commit_hash = commit_hash;
	}



	public String getRepo_url() {
		return repo_url;
	}



	public void setRepo_url(String repo_url) {
		this.repo_url = repo_url;
	}



	public Timestamp getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public SalesforceOrg getSforg() {
		return sforg;
	}



	public void setSforg(SalesforceOrg sforg) {
		this.sforg = sforg;
	}



	public CommitHistory(String commit_hash, String repo_url, Timestamp timestamp, User user, SalesforceOrg sforg) {
		super();
		this.commit_hash = commit_hash;
		this.repo_url = repo_url;
		this.timestamp = timestamp;
		this.user = user;
		this.sforg = sforg;
	}



	public CommitHistory() {
		super();
		// TODO Auto-generated constructor stub
	} 
}
