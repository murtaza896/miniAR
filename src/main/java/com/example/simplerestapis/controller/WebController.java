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
	
		
	@GetMapping("/")
	public ModelAndView welcome() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	@GetMapping("/new")
	public ModelAndView authorized(@RequestParam String code) 
	{
		String organizationId = SFservice.authenticateOrg(code);
		
		ModelAndView mv = new ModelAndView();
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


