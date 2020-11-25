package com.example.simplerestapis.models;
import java.sql.Timestamp;

public class CommitHistoryResponse {
	
	String git_username;
	Timestamp timestamp;
	String repo_name;
	String repo_url;
	String commit_hash;
	String commit_msg;
	String nick_name;
	public String getGit_username() {
		return git_username;
	}
	public void setGit_username(String git_username) {
		this.git_username = git_username;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getRepo_name() {
		return repo_name;
	}
	public void setRepo_name(String repo_name) {
		this.repo_name = repo_name;
	}
	public String getRepo_url() {
		return repo_url;
	}
	public void setRepo_url(String repo_url) {
		this.repo_url = repo_url;
	}
	public String getCommit_hash() {
		return commit_hash;
	}
	public void setCommit_hash(String commit_hash) {
		this.commit_hash = commit_hash;
	}
	public String getCommit_msg() {
		return commit_msg;
	}
	public void setCommit_msg(String commit_msg) {
		this.commit_msg = commit_msg;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public CommitHistoryResponse(String git_username, Timestamp timestamp, String repo_name, String repo_url,
			String commit_hash, String commit_msg, String nick_name) {
		super();
		this.git_username = git_username;
		this.timestamp = timestamp;
		this.repo_name = repo_name;
		this.repo_url = repo_url;
		this.commit_hash = commit_hash;
		this.commit_msg = commit_msg;
		this.nick_name = nick_name;
	}
	public CommitHistoryResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
