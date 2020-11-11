package com.example.simplerestapis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.simplerestapis.models.SalesforceOrg;
import com.example.simplerestapis.models.User;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.SalesforceService;
import com.example.simplerestapis.service.UserService;

import ch.qos.logback.classic.Logger;
import java.sql.Timestamp;    
import java.util.Date;

@RestController
public class WebController {
			
	@Autowired
	private UserService service;
	
	@Autowired
	private SalesforceService SFservice;
	
	@Autowired
	private FileBasedDeployAndRetrieve fbd;
	
	@Autowired
	private Environment env;
	
	@GetMapping("/")
	public ModelAndView welcome() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	@GetMapping("/new")
	public ModelAndView authorized(@RequestParam String code) 
	{
		
		ModelAndView mv = new ModelAndView();
		
		String accessToken;
		String refreshToken;
		String clientId = env.getProperty("app.client_id");
		String clientSecret = env.getProperty("app.client_secret");
		String organizationId;
		String issuedAt;
		String identityUrl;
		String instanceUrl;
		
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String url = "https://login.salesforce.com/services/oauth2/token";
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("client_id", clientId);
		map.add("redirect_uri", "http://localhost:8080/new");
		map.add("client_secret", clientSecret);
		map.add("code", code);
		map.add("grant_type", "authorization_code");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
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
		
		SalesforceOrg org = new SalesforceOrg(organizationId, accessToken, refreshToken, clientId, clientSecret, identityUrl, instanceUrl, issuedAt);
		SFservice.addOrg(org);
		
		mv.addObject("orgId",organizationId );
		mv.setViewName("retrieve");
		return mv;
	}

	
	@GetMapping("/retrieve/{orgId}")
	public ModelAndView retrieveData(@PathVariable String orgId) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("retrieve");
		try {
			fbd.createMetadataConnection("retrieve",orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@GetMapping("/deploy/{orgId}")
	public ModelAndView deployData(@PathVariable String orgId) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("retrieve");
		try {
			fbd.createMetadataConnection("deploy",orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
//	@RequestMapping("/get-all-users")
//	public List<User> getAllUsers() 
//	{
//		return service.getAllUsers();
//	}
//	
//	@GetMapping("/get-user/{id}")
//	public User getUserById(@PathVariable int id) 
//	{
//		return service.getUserById(id);
//	}
//	
//	@PostMapping("/add-user")
//	public User addUser(@RequestBody User inputPayload) {
//		return service.addUser(inputPayload);
//	}
//	
//	@PutMapping("/update-user")
//	public User updateUser(@RequestBody User new_user) 
//	{
//		return  service.updateUser(new_user);
//	}
//	
//	@DeleteMapping("/delete-user")
//	public String deleteUser(@RequestParam int id) 
//	{
//		return service.deleteUser(id);
//	}
}


