package com.example.simplerestapis.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.example.simplerestapis.service.SalesforceService;
//import com.example.simplerestapis.service.UserService;
import com.example.simplerestapis.service.UtilService;

@CrossOrigin(origins = "${app.angular.hosturi}" , allowCredentials = "true")
@RestController
@RequestMapping("/org")
public class OrgController {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private SalesforceService SFservice;
	
	@Autowired 
	private UtilService utilService;
	
//	@Autowired
//	private UserService userService;
	
	@GetMapping("/new-org")
	public ResponseEntity<?> authorizeOrg(@RequestParam(required = false) String code, HttpServletRequest request) 
	{
		SFservice.authorizeOrg(code, request);
		ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.OK);
		return responseEntity;
		// RedirectView redirectView = new RedirectView();
		// redirectView.setUrl(env.getProperty("app.angular.pages.settings"));
	    // return redirectView;
	}
	
	@GetMapping("/list-orgs")
	public ArrayList<Map<String, String>> getOrgList(HttpServletRequest request){
		
//		String user_id = utilService.readCookie(request, "user_id");
//		String user_id = userService.getIdByEmail(request.getAttribute("email").toString()) + "";
		String user_id = utilService.readCookie(request, "user_id");
//		System.out.println("Cookie user id:" + user_id);
		return SFservice.getOrgList(user_id);
	}
	
	@PostMapping("/add-webhook")
	public ResponseEntity<?> addWebhook(@RequestBody String body, HttpServletRequest request){
		JSONObject obj = new JSONObject(body);
		String webhook_url = obj.getString("webhook_url");
//		int userId = utilService.readIdFromToken(request);
		String user_id = utilService.readCookie(request, "user_id");
//		User user = userService.getUserById(userId);
//		user.setWebhook_url(webhook_url);
//		userService.updateUser(user);
		
		//save webhook url in mongo via node service
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		String payload = "{\"text\" : \"MiniAR application added\"}";
		HttpEntity<String> request1 = new HttpEntity<String>(payload, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(webhook_url, request1, String.class);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
