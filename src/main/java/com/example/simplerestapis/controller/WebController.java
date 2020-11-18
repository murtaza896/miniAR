package com.example.simplerestapis.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.example.simplerestapis.models.GitStore;
import com.example.simplerestapis.models.SalesforceOrg;
import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.GitAccountsService;
import com.example.simplerestapis.service.GitStoreService;
import com.example.simplerestapis.service.SalesforceService;
import com.example.simplerestapis.service.UserService;

@RestController
public class WebController {
			
	@Autowired
	private UserService service;
	
	@Autowired
	private SalesforceService SFservice;
	
	@Autowired
	private GitStoreService gitStoreService;
	
	@Autowired
	private FileBasedDeployAndRetrieve fbd;
	
	@Autowired
	private GitAccountsService gitAccountsService;
	
	@GetMapping("/")
	public ModelAndView welcome() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	
	@PostMapping("/register")
	public User register(@RequestBody User user) {
		return service.addUser(user);
	}
	
	@PostMapping("/login")
	public User login(@RequestBody userCredentials user, HttpServletResponse response) {
		User user1= service.validateUser(user);
		if(user1 != null && user1.getPassword().equals(user.password)) {
			System.out.println(user1.toString());
			Cookie cookie = new Cookie("user_id", String.valueOf(user1.getId()));
			
			response.addCookie(cookie);
			return user1;
		}
		return null;
	}
	
	@GetMapping("/addToken")
	public String xyz(HttpServletRequest request , HttpServletResponse response) {
		Cookie cookie = new Cookie("user_id", "1");
		response.addCookie(cookie);
		Cookie cookies[] = request.getCookies();
		for(Cookie c : cookies) {
			if(c.getName().equals("user_id"))
				return c.getValue();
		}
		
		return "";
	}
	
	

	@GetMapping("/new-org")
	public String authorizeOrg(@RequestParam(required = false) String code, HttpServletRequest request) 
	{
		return SFservice.authorizeOrg(code, request);
	}
	
	@GetMapping("/list-orgs")
	public ArrayList<Map<String, String>> getOrgList(HttpServletRequest request){
		String user_id = SFservice.readCookie(request, "user_id");
		ArrayList<SalesforceOrg> sfOrgs =  SFservice.getOrgList(user_id);
		ArrayList<Map<String, String >> res = new ArrayList<Map<String,String>>();
		
		for(SalesforceOrg sfOrg : sfOrgs) {
			Map<String, String > mp = new HashMap<String, String>();
			mp.put("org_id", sfOrg.getId());
			mp.put("org_label", sfOrg.getInstanceUrl().substring(8));
			res.add(mp);
		}
		
		return res;
	}

	
//	@GetMapping("/new-org")
//	public ModelAndView authorized(@RequestParam String code) 
//	{
//		String organizationId = SFservice.authorizeOrg(code);
//		
//		ModelAndView mv = new ModelAndView();
//		mv.addObject("orgId",organizationId );
//		mv.setViewName("retrieve");
//		return organizationId;
//	
//	}

	@GetMapping("/oauth-git")
	public ModelAndView oauthGit() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("oauthGit");
		return mv;
	}
	
	
	@GetMapping("/new-repo")
	public int authorizeGitAcc(@RequestParam String code, HttpServletRequest request) {
		String userId = SFservice.readCookie(request, "user_id");
		return gitAccountsService.authorizeGitAcc(code, userId);
	}
	
	
	@CrossOrigin("https://localhost:4200")
	@GetMapping("/list-repos/{accountId}")
	public ArrayList<GitStore> listRepos(@PathVariable int accountId, HttpServletRequest request)
	{	
		String userId = SFservice.readCookie(request, "user_id");
		return gitStoreService.listRepos(accountId, Integer.parseInt(userId));
	}
	
//	@GetMapping("/list-mapped-repos/{org_id}")
//	public ArrayList<Map<String, String>> listMappedRepos(@PathVariable String org_id)
//	{	
//		return gitStoreService.listMappedRepos(org_id);
//	}
	
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
	
}


