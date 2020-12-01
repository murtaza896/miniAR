package com.example.simplerestapis.service;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.simplerestapis.models.CommitHistory;
import com.example.simplerestapis.repository.CommitHistoryRepository;

import net.lingala.zip4j.ZipFile;

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
	
	
	@Autowired
	FileBasedDeployAndRetrieve fileBasedDR;
	
	
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
	
	public Boolean gitCommit(String path, String message, String username , String repoUrl, int userId, String orgId,String repoName, String gitUsername, String accountId, String repoId) {
		
		try {
			Git git = Git.open(new File(path));
			git.add().addFilepattern(".").call();
			RevCommit rc = git.commit().setAuthor("Rohit", username).setMessage(message).call();
			String committime = rc.getCommitTime() + "000";
			long x = Long.parseLong(committime);
			Timestamp timestamp =  new Timestamp(x);
			System.out.println("TimeStamp:" + timestamp);
			System.out.println(rc.getName());
			CommitHistory commitHistory  = new CommitHistory(rc.getName(), repoUrl, timestamp ,rc.getFullMessage(),  repoName, gitUsername, repoId, userService.getUserById(userId), gitAccountsService.getUserById(Integer.parseInt(accountId)) ,salesforceService.getOrg(orgId) );
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
	
	public void testDeploy(String accessToken, String repoUrl, String commithash, String path, String targetOrgId, String repoId, int userId, String orgId ) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		//System.out.println("path value is::" + path);
		
		File file = new File(path);
		if(file.exists()) {
			deleteFolder(file);
		}
		
		System.out.println(file);
		Git git = Git.cloneRepository().setURI(repoUrl).setDirectory(file).setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, "")).call();	
		System.out.println("cloned /.............");
		System.out.println(git.checkout().setName(commithash).call());
		git.close();
		
		new ZipFile(path + ".zip").addFolder(new File(path+"\\unpackaged"));
		String url = userService.getUserById(userId).getWebhook_url();
		try {
			fileBasedDR.createMetadataConnection("deploy", orgId, userId, repoId, targetOrgId);
			if(url!=null) {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");
				String payload = "{\r\n" + 
						"  \"@type\": \"MessageCard\",\r\n" + 
						"  \"@context\": \"http://schema.org/extensions\",\r\n" + 
						"  \"themeColor\": \"0076D7\",\r\n" + 
						"  \"summary\": \"Salesforce Deployment from miniAR\",\r\n" + 
						"  \"sections\": [{\r\n" + 
						"      \"activityTitle\": \"Salesforce Deployment from miniAR\",\r\n" + 
						"      \"activitySubtitle\": \"Status\",\r\n" + 
						"      \"activityImage\": \"https://cdn2.iconfinder.com/data/icons/web-and-apps-interface/32/OK-512.png\",\r\n" + 
						"      \"facts\": [{\r\n" + 
						"          \"name\": \"From\",\r\n" + 
						"          \"value\": \"" + orgId + "\""+
						"      }, {\r\n" + 
						"          \"name\": \"To\",\r\n" + 
						"          \"value\": \"" + targetOrgId + "\""+
						"      }, {\r\n" + 
						"          \"name\": \"Status\",\r\n" + 
						"          \"value\": \"Successful\"\r\n" + 
						"      }],\r\n" + 
						"      \"markdown\": true\r\n" + 
						"  }]\r\n" + 
						"}";
				HttpEntity<String> request = new HttpEntity<String>(payload, headers);
				ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			}
			
		} catch (Exception e) {
			if(url != null) {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");
				String payload = "{\r\n" + 
						"  \"@type\": \"MessageCard\",\r\n" + 
						"  \"@context\": \"http://schema.org/extensions\",\r\n" + 
						"  \"themeColor\": \"0076D7\",\r\n" + 
						"  \"summary\": \"Salesforce Deployment from miniAR\",\r\n" + 
						"  \"sections\": [{\r\n" + 
						"      \"activityTitle\": \"Salesforce Deployment from miniAR\",\r\n" + 
						"      \"activitySubtitle\": \"Status\",\r\n" + 
						"      \"activityImage\": \"https://cdn3.iconfinder.com/data/icons/simple-web-navigation/165/cross-512.png\",\r\n" + 
						"      \"facts\": [{\r\n" + 
						"          \"name\": \"From\",\r\n" + 
						"          \"value\": \"" + orgId + "\""+
						"      }, {\r\n" + 
						"          \"name\": \"To\",\r\n" + 
						"          \"value\": \"" + targetOrgId + "\""+
						"      }, {\r\n" + 
						"          \"name\": \"Status\",\r\n" + 
						"          \"value\": \"Failed\"\r\n" + 
						"      }],\r\n" + 
						"      \"markdown\": true\r\n" + 
						"  }]\r\n" + 
						"}";
				HttpEntity<String> request = new HttpEntity<String>(payload, headers);
				ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			}
			e.printStackTrace();
		}
	}
}
