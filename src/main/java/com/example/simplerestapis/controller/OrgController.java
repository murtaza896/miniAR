package com.example.simplerestapis.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.simplerestapis.service.SalesforceService;
import com.example.simplerestapis.service.UtilService;

@CrossOrigin(origins = "*" , allowCredentials = "true")
@RestController
@RequestMapping("/org")
public class OrgController {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private SalesforceService SFservice;
	
	@Autowired 
	private UtilService utilService;
	
	@GetMapping("/new-org")
	public RedirectView authorizeOrg(@RequestParam(required = false) String code, HttpServletRequest request) 
	{
		SFservice.authorizeOrg(code, request);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(env.getProperty("app.angular.pages.settings"));
	    return redirectView;
	}
	
	@GetMapping("/list-orgs")
	public ArrayList<Map<String, String>> getOrgList(HttpServletRequest request){
		
		String user_id = utilService.readCookie(request, "user_id");
		System.out.println("Cookie user id:" + user_id);
		return SFservice.getOrgList(user_id);
	}
	
	
}
