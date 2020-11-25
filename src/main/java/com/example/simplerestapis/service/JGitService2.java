package com.example.simplerestapis.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplerestapis.models.CommitHistory;
import com.example.simplerestapis.repository.CommitHistoryRepository;

@Service
public class JGitService2 {
	
	@Autowired
	GitAccountsService gitAccountsService;
	
	@Autowired
	CommitHistoryRepository commitHistoryRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SalesforceService salesforceService; 
	
	
	public Boolean gitClone(String accessToken, String repoUrl, String path)
	{
		try {
			File dir = new File(path);
			if(dir.exists()) {
				deleteFolder(dir);
			}
			Git git = Git.cloneRepository()
					.setURI(repoUrl)
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, ""))
					.setDirectory(new File(path))
					.call();
			System.out.println("Cloning....");
			System.out.println("Colne successful :: " + path);
			git.close();
			
//			return "checkout successful";
			return true;
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public void copyDirectory(File sourceDir, File targetDir) throws IOException {
		if (sourceDir.isDirectory()) {
			copyDirectoryRecursively(sourceDir, targetDir);
		} else {
			Files.copy(sourceDir.toPath(), targetDir.toPath(),StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void copyDirectoryRecursively(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String child : source.list()) {
			copyDirectory(new File(source, child), new File(target, child));
		}
	}
	
	public Boolean gitCommit(String path, String message, String username , String repoUrl, int userId, String orgId,String repoName, String gitUsername) {
		
		try {
			Git git = Git.open(new File(path));
			git.add().addFilepattern(".").call();
			RevCommit rc = git.commit().setAuthor("Rohit", username).setMessage(message).call();
			String committime = rc.getCommitTime() + "000";
			long x = Long.parseLong(committime);
			Timestamp timestamp =  new Timestamp(x);
			System.out.println("TimeStamp:" + timestamp);
			System.out.println(rc.getName());
			CommitHistory commitHistory  = new CommitHistory(rc.getName(), repoUrl, timestamp ,rc.getFullMessage(),  repoName, gitUsername, userService.getUserById(userId), salesforceService.getOrg(orgId) );
			commitHistoryRepository.save(commitHistory);
			System.out.println(git.toString());
			System.out.println("Commit Successful");
			git.close();
			return true;
		} catch (IOException | GitAPIException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public Boolean gitPush(String accessToken, String path) {
		try {
			Git git = Git.open(new File(path));
			git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, "")).call();
			git.close();
			return true;
		} catch (IOException | GitAPIException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public void deleteFolder(File file) {
		for (File subFile : file.listFiles()) {
			if (subFile.isDirectory()) {
				deleteFolder(subFile);
			} else {
				subFile.delete();
			}
		}
		file.delete();
	}
}
