package com.example.simplerestapis.models;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "todos")
public class Git {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	
	private String username;

	private String password;
	
	private String repoURL;
	
	private String message;
	
	
	
	public Git() {
		super();
	}

	public Git( String username, String password, String repoURL, String message, boolean isDone) {
		super();
		this.username = username;
		this.password = password;
		this.repoURL = repoURL;
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRepoURL() {
		return repoURL;
	}
	
	public void setRepoURL(String repoURL) {
		this.repoURL = repoURL;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}