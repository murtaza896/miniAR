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
	
	@Column(name="commit_msg")
	String commit_msg;
	
	@Column(name="repo_name")
	String repo_name;
	
	@Column(name = "git_username")
	String git_username;
	
	@Column(name = "repo_id")
	String repo_id;
	
	@ManyToOne(fetch = FetchType.EAGER,optional= false)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.EAGER,optional= false)
	@JoinColumn(name = "account_id")
	private GitAccounts gitAccount;
	
	@ManyToOne(fetch = FetchType.EAGER,optional= false)
	@JoinColumn(name = "sforg_id")
	private SalesforceOrg sforg;



	public String getRepo_id() {
		return repo_id;
	}


	public void setRepo_id(String repo_id) {
		this.repo_id = repo_id;
	}


	public String getGit_username() {
		return git_username;
	}


	public void setGit_username(String git_username) {
		this.git_username = git_username;
	}


//	public CommitHistory(String commit_hash, String repo_url, Timestamp timestamp, String commit_msg, String repo_name,
//			String git_username, User user, SalesforceOrg sforg) {
//		super();
//		this.commit_hash = commit_hash;
//		this.repo_url = repo_url;
//		this.timestamp = timestamp;
//		this.commit_msg = commit_msg;
//		this.repo_name = repo_name;
//		this.git_username = git_username;
//		this.user = user;
//		this.sforg = sforg;
//	}

//	public CommitHistory(String commit_hash, String repo_url, Timestamp timestamp, String commit_msg, String repo_name,
//			String git_username, User user, GitAccounts gitAccount, SalesforceOrg sforg) {
//		super();
//		this.commit_hash = commit_hash;
//		this.repo_url = repo_url;
//		this.timestamp = timestamp;
//		this.commit_msg = commit_msg;
//		this.repo_name = repo_name;
//		this.git_username = git_username;
//		this.user = user;
//		this.gitAccount = gitAccount;
//		this.sforg = sforg;
//	}


	public GitAccounts getGitAccount() {
		return gitAccount;
	}


	public CommitHistory(String commit_hash, String repo_url, Timestamp timestamp, String commit_msg, String repo_name,
		String git_username, String repo_id, User user, GitAccounts gitAccount, SalesforceOrg sforg) {
	super();
	this.commit_hash = commit_hash;
	this.repo_url = repo_url;
	this.timestamp = timestamp;
	this.commit_msg = commit_msg;
	this.repo_name = repo_name;
	this.git_username = git_username;
	this.repo_id = repo_id;
	this.user = user;
	this.gitAccount = gitAccount;
	this.sforg = sforg;
}


	@Override
	public String toString() {
		return "CommitHistory [id=" + id + ", commit_hash=" + commit_hash + ", repo_url=" + repo_url + ", timestamp="
				+ timestamp + ", commit_msg=" + commit_msg + ", repo_name=" + repo_name + ", git_username="
				+ git_username + ", repo_id=" + repo_id + ", user=" + user + ", gitAccount=" + gitAccount + ", sforg="
				+ sforg + "]";
	}


	public void setGitAccount(GitAccounts gitAccount) {
		this.gitAccount = gitAccount;
	}


	public String getCommit_msg() {
		return commit_msg;
	}






	public void setCommit_msg(String commit_msg) {
		this.commit_msg = commit_msg;
	}






	public String getRepo_name() {
		return repo_name;
	}






	public void setRepo_name(String repo_name) {
		this.repo_name = repo_name;
	}






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
		System.out.println(timestamp.toInstant());
		System.out.println(timestamp.getTime());
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



//	public CommitHistory(String commit_hash, String repo_url, Timestamp timestamp, User user, SalesforceOrg sforg) {
//		super();
//		this.commit_hash = commit_hash;
//		this.repo_url = repo_url;
//		this.timestamp = timestamp;
//		this.user = user;
//		this.sforg = sforg;
//	}



	public CommitHistory() {
		super();
		// TODO Auto-generated constructor stub
	} 
}
