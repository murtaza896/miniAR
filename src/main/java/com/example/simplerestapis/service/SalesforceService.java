package com.example.simplerestapis.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.simplerestapis.models.SalesforceOrg;
import com.example.simplerestapis.repository.SalesforceOrgRepository;
import com.example.simplerestapis.repository.UserRepository;

@Service
public class SalesforceService {

	@Autowired
	private SalesforceOrgRepository repository;

	@Autowired
	private Environment env;

//	String authToken;

	public SalesforceOrg addOrg(SalesforceOrg obj) {
		return repository.save(obj);
	}

	public SalesforceOrg getOrg(String orgId) {
		return repository.findById(orgId).orElse(null);
	}

	public String renewAccess(String orgId) {
		RestTemplate restTemplate = new RestTemplate();
		SalesforceOrg sfOrg = getOrg(orgId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//		String url = "https://login.salesforce.com/services/oauth2/token";
		String url = env.getProperty("app.sf.access_token.uri");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("client_id", env.getProperty("app.sf.client_id"));
		map.add("redirect_uri", env.getProperty("app.sf.redirect.uri2"));
		map.add("client_secret", env.getProperty("app.sf.client_secret"));
		map.add("refresh_token", sfOrg.getRefreshToken());
		map.add("grant_type", "refresh_token");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		JSONObject obj1 = new JSONObject(response.getBody());
		System.out.println("response obj: " + response);
		String token = obj1.getString("access_token");
		System.out.println("response obj: " + token);
		sfOrg.setAccessToken(token);
		repository.save(sfOrg);
		return token;
	}

	public String authorizeOrg(String code) {

		String accessToken;
		String refreshToken;
		String clientId = env.getProperty("app.sf.client_id");
		String clientSecret = env.getProperty("app.sf.client_secret");
		String organizationId;
		String issuedAt;
		String identityUrl;
		String instanceUrl;

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//		String url = "https://login.salesforce.com/services/oauth2/token";
		String url = env.getProperty("app.sf.access_token.uri");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("client_id", clientId);

		map.add("redirect_uri", env.getProperty("app.sf.redirect.uri2"));

		map.add("client_secret", clientSecret);
		map.add("code", code);
		map.add("grant_type", "authorization_code");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		JSONObject obj = new JSONObject(response.getBody());

		accessToken = obj.getString("access_token");
		refreshToken = obj.getString("refresh_token");
		instanceUrl = obj.getString("instance_url");
		identityUrl = obj.getString("id");
		issuedAt = obj.getString("issued_at");

		String url2 = identityUrl;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url2);

		MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
		vars.add("oauth_token", accessToken);
		vars.add("format", "json");
		builder.queryParams(vars);

		String response2 = restTemplate.getForObject(builder.build().encode().toUriString(), String.class, vars);
		JSONObject obj2 = new JSONObject(response2);

		organizationId = obj2.getString("organization_id");

		SalesforceOrg org = new SalesforceOrg(organizationId, accessToken, refreshToken, identityUrl, instanceUrl,
				issuedAt);
		this.addOrg(org);

		return organizationId;

	}

}
