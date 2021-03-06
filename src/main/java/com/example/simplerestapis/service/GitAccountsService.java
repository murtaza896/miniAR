package com.example.simplerestapis.service;

import java.util.ArrayList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.simplerestapis.models.GitAccounts;
import com.example.simplerestapis.models.UserGitAcc;
import com.example.simplerestapis.models.UserGitAccPK;
import com.example.simplerestapis.repository.GitAccountsRepository;
import com.example.simplerestapis.repository.UserGitAccRepository;

@Service
public class GitAccountsService {

	@Autowired
	private GitAccountsRepository gitAccountsRepository;
	
	@Autowired
	private UserGitAccRepository userGitAccRepository;
	
	@Autowired
	private Environment env;
	
//	@Autowired
//	private UserService userService;
	
	public GitAccounts getUserById(int id) {
		return gitAccountsRepository.findById(id).orElse(null);
	}
	
	public int authorizeGitAcc(String code, String userId) {
		int accId;
		String username;
		String avatarUrl; 
		String accessToken;
//		String user_id;
		String clientId = env.getProperty("app.git.client_id");
		String clientSecret = env.getProperty("app.git.client_secret");
		
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Accept", "application/json");
		String url = env.getProperty("app.git.access_token.uri");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		map.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		JSONObject obj = new JSONObject(response.getBody());
		
		accessToken = obj.getString("access_token");
		System.out.println("Access Token: " + accessToken);

		String url3 = env.getProperty("app.git.getuser.uri");
		
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setContentType(MediaType.APPLICATION_JSON);
		headers2.set("Authorization","Bearer " + accessToken);
		
		HttpEntity<String> request2 = new HttpEntity<String>(headers2);

		ResponseEntity<String> response3 = restTemplate.exchange(url3, HttpMethod.GET, request2, String.class);
		
		JSONObject obj3 = new JSONObject(response3.getBody());
		accId = obj3.getInt("id");
		username = obj3.getString("login");
		avatarUrl = obj3.getString("avatar_url");
		System.out.println(obj3);
//		user = userService.getUserById(Integer.parseInt(userId));
		
		GitAccounts gitAccount = new GitAccounts(accId, username, avatarUrl,accessToken);
		gitAccountsRepository.save(gitAccount);
		
		UserGitAccPK userGitAccPK = new UserGitAccPK(userId, gitAccount);
		UserGitAcc userGitAcc = new UserGitAcc(userGitAccPK, userId);
		userGitAccRepository.save(userGitAcc);
		
		return accId;
	}

	public ArrayList<GitAccounts> listGitAccounts(String userId) {
		
		ArrayList<UserGitAcc> userGitAccs = userGitAccRepository.findByUserId(userId);
		ArrayList<GitAccounts> res = new ArrayList<GitAccounts>();
		
		for(UserGitAcc x: userGitAccs)
		{
			res.add(x.getUserGitAccPK().getGitacc());
		}
//		return gitAccountsRepository.findByUserId(userId);
		return res;
	}
}
