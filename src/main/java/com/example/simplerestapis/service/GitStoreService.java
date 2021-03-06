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
import org.springframework.web.client.RestTemplate;
import com.example.simplerestapis.models.GitAccounts;
import com.example.simplerestapis.models.GitStore;

@Service
public class GitStoreService {
	
	@Autowired
	private GitAccountsService gitAccountsService;
	
	@Autowired
	private Environment env;
	

	public ArrayList<GitStore> listRepos(int accountId, String userId) {
		RestTemplate restTemplate = new RestTemplate();
		String url2 = env.getProperty("app.git.getrepos.uri");
		String url3 = env.getProperty("app.git.getuser.uri");
		
		GitAccounts account = gitAccountsService.getUserById(accountId);
		
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setContentType(MediaType.APPLICATION_JSON);
		headers2.set("Authorization","Bearer " + account.getAccess_token());
		
		HttpEntity<String> request2 = new HttpEntity<String>(headers2);

		ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.GET, request2, String.class);
		ResponseEntity<String> response3 = restTemplate.exchange(url3, HttpMethod.GET, request2, String.class);
		
		
		JSONArray obj2 = new JSONArray(response2.getBody());
		JSONObject obj3 = new JSONObject(response3.getBody());
		ArrayList<GitStore> res = new ArrayList<GitStore>();
		for(int i = 0; i < obj2.length(); i++ )
		{
			JSONObject temp = obj2.getJSONObject(i);
			System.out.println(temp.getInt("id"));
			GitStore tempRepo = new GitStore(temp.getInt("id") + "", temp.getString("html_url"), temp.getString("name"), obj3.getString("login"), userId);
			res.add(tempRepo);
		}
		System.out.println(res);
		return res;
	}
	
//	public ArrayList<Map<String,String>> listMappedRepos(String orgId){
//		ArrayList<GitStore> repoList= repository.findByorg_id(orgId);
//		ArrayList<Map<String, String>> res = new ArrayList<Map<String,String>>();
//		for(GitStore repo : repoList) {
//			Map<String, String> mp = new HashMap<String, String>();
//			mp.put("repo_id", repo.getRepoId());
//			mp.put("repo_label", repo.getRepoName());
//			res.add(mp);
//		}
//		return res;
//	}
}

