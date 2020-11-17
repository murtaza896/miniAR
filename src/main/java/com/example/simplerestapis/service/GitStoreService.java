package com.example.simplerestapis.service;

import java.util.ArrayList;

import org.json.JSONArray;
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

import com.example.simplerestapis.models.GitRepo;
import com.example.simplerestapis.models.GitStore;
import com.example.simplerestapis.repository.GitStoreRepository;

@Service
public class GitStoreService {
	
	@Autowired
	private GitStoreRepository repository;
	
	@Autowired
	private Environment env;
	
	public GitStore addRepo(GitStore obj) {
		return repository.save(obj);
	}

	public GitStore getRepo(String repoId) {
		return repository.findById(repoId).orElse(null);
	}
	
	public String authorizeGitAcc(String code) {

		String accessToken;
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
		
		return accessToken;
	}

	public ArrayList<GitRepo> listRepos(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		
		String url2 = env.getProperty("app.git.getrepos.uri");
		String url3 = env.getProperty("app.git.getuser.uri");
		
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setContentType(MediaType.APPLICATION_JSON);
		headers2.set("Authorization","Bearer " + accessToken);
		
		HttpEntity<String> request2 = new HttpEntity<String>(headers2);

		ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.GET, request2, String.class);
		ResponseEntity<String> response3 = restTemplate.exchange(url3, HttpMethod.GET, request2, String.class);
		
		
		JSONArray obj2 = new JSONArray(response2.getBody());
		JSONObject obj3 = new JSONObject(response3.getBody());
		ArrayList<GitRepo> res = new ArrayList<GitRepo>();
		
		for(int i = 0; i < obj2.length(); i++ )
		{
			JSONObject temp = obj2.getJSONObject(i);
			System.out.println(temp.getInt("id"));
			GitRepo tempRepo = new GitRepo(temp.getInt("id") + "", temp.getString("name"), temp.getString("html_url"), obj3.getString("login"));
			res.add(tempRepo);
		}
		
		return res;
	}
}

